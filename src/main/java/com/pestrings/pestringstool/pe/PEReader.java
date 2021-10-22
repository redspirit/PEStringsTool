package com.pestrings.pestringstool.pe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PEReader {

    private ByteBuffer buffer;
    public List<PEStringItem> strings = new ArrayList<>();
    public PEHeader headers = new PEHeader();
    public String filePath;
    public boolean isStrictFilter = true;

    // convert hex to int
    public long toInt(String hex) {
        return Long.parseLong(hex, 16);
    }

    // convert int to hex
    public String toHex(long val) {
        return Long.toHexString(val);
    }

    public ByteBuffer loadFileToBuffer(String path) {
        byte[] bytes;
        try {
            // todo проверить на неверном файле
            bytes = Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb;
    }

    public boolean loadFile(String path) {

        this.buffer = loadFileToBuffer(path);
        if(this.buffer == null) return false;

        this.filePath = path;
        this.headers.loadBuffer(this.buffer);
        if(!this.headers.signature.equals("PE")) return false;

        this.extractStrings();

        return true;

    }

    public void extractStrings() {

        int startOfSections = this.headers.sections.get(0).pointerToRawData;
        byte[] unicodeChar = new byte[2];
        boolean isGrouping = false;
        int startAddr = 0;
        Pattern ptn = Pattern.compile("[ 0-9a-zа-яё§!@#$%^&*()_+=><.\\\\\\[\\]?`'~|/-]+",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE);

        for (int i = startOfSections; i < buffer.capacity() - 1; i++) {

            byte code = buffer.get(i);
            buffer.slice(i, 2).get(unicodeChar);
            String uft8Char = new String(unicodeChar, StandardCharsets.UTF_8);
            boolean isLatter = Character.isLetter(uft8Char.charAt(0)) && uft8Char.length() == 1;
            boolean isCodeLatter = code > 32 && code <= 126;

            if((isCodeLatter || isLatter) && !isGrouping) {
                // start group
                isGrouping = true;
                startAddr = i;
            }
            if(code == 0 && isGrouping) {
                // end group

                isGrouping = false;
                int len = i - startAddr;
                if (len > 2) {
                    byte[] chars = new byte[len];
                    buffer.slice(startAddr, len).get(chars);
                    String s = new String(chars, StandardCharsets.UTF_8);
                    if(ptn.matcher(s).matches()) {
                        String sectionName = headers.getSectionByOffset(startAddr).name;
                        if(!sectionName.equals(".text") && !sectionName.equals(".reloc")) {
                            this.strings.add(new PEStringItem(startAddr, s));
                        }
                    }

                }

            }

        }

    }

    public List<PEStringItem> searchTexts (String text, boolean isEqual, boolean isCase) {

        List<PEStringItem> tmpList = strings;
        if(isStrictFilter) {
            Pattern p = Pattern.compile("[a-z]{3,}", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            tmpList = strings.stream()
                    .filter(item -> p.matcher(item.data).find())
                    .collect(Collectors.toList());
        }

        if(text.equals("")) return tmpList;
        return tmpList.stream()
                .filter(item -> {
                    if(isEqual) return item.data.equals(text);
                    if(isCase) {
                        return item.data.contains(text);
                    } else {
                        return item.data.toLowerCase().contains(text.toLowerCase());
                    }
                })
                .collect(Collectors.toList());
    }

    public PESection getSectionByOffset(int offset) {

        List<PESection> match = this.headers.sections.stream()
                .filter(section -> {
                    int start = section.pointerToRawData;
                    int end = start + section.sizeOfRawData;
                    return offset >= start && offset < end;
                })
                .collect(Collectors.toList());

        return match.get(0);

    }

    public int convertOffsetToVirtual(int offset) {
        PESection section = this.getSectionByOffset(offset);
        if(section == null) return -1;
        return offset - section.pointerToRawData + section.virtualAddress + this.headers.imageBase;
    }

    public List<Integer> findXref(int offset) {

        int startOfSections = this.headers.sections.get(0).pointerToRawData;
        int address = this.convertOffsetToVirtual(offset);
        List<Integer> list = new ArrayList<>();
        for (int i = startOfSections; i < this.buffer.capacity() - 4; i++) {
            if(this.buffer.getInt(i) == address) {
                list.add(i);
            }
        }
        return list;

    }

    public int applyChanges(List<PEReplaceItem> items, String fileName) {

        int textsLen = 0;
        ByteBuffer cloneBuf = loadFileToBuffer(this.filePath); // read file again
        cloneBuf.order(ByteOrder.LITTLE_ENDIAN);

        for (PEReplaceItem item : items) {
            item.bytes = item.newText.getBytes(StandardCharsets.UTF_8);
            item.localAddr = textsLen;
            textsLen += (item.bytes.length + 2);
        }

        int secAlignment = this.headers.sectionAlignment;
        int contentVirtLen = (int) Math.ceil(textsLen / 64.0) * 64;
        int contentLen = (int) Math.ceil(textsLen / 512.0) * 512;
        ByteBuffer content = ByteBuffer.allocate(contentLen);
        content.order(ByteOrder.LITTLE_ENDIAN);

        for (PEReplaceItem item : items) {
            content.put(item.localAddr, item.bytes);
        }

        ByteBuffer buf = ByteBuffer.allocate(cloneBuf.capacity() + content.capacity());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(cloneBuf);

        int peHeaderAddress = this.headers.e_lfanew;
        PESection lastSection = this.headers.sections.get(this.headers.sections.size() - 1);
        int headerAddr = lastSection.headerPointer + 40;

        int sectionsAddr = cloneBuf.capacity();
        buf.putShort(peHeaderAddress + 6, (short) (this.headers.numberOfSections + 1));
        buf.putInt(peHeaderAddress + 80, this.headers.sizeOfImage + content.capacity());

        int secVirtAddr = (int) (Math.ceil((lastSection.virtualSize + lastSection.virtualAddress) / (float) secAlignment) * secAlignment);

        buf.position(headerAddr);
        buf.put(".pestool".getBytes(StandardCharsets.UTF_8)); // name
        buf.putInt(headerAddr + 8, contentVirtLen); // virtualSize
        buf.putInt(headerAddr + 12, secVirtAddr); // virtualAddress
        buf.putInt( headerAddr + 16, content.capacity()); // sizeOfRawData
        buf.putInt( headerAddr + 20, sectionsAddr); // pointerToRawData
        buf.putInt(headerAddr + 36, (int) this.toInt("C0000000")); // characteristics

        buf.position(sectionsAddr);
        buf.put(content);

        for (PEReplaceItem item : items) {
            int va = item.localAddr + secVirtAddr + this.headers.imageBase;
            for(int xref : this.findXref(item.stringItem.offset)) {
                buf.putInt(xref, va);
            }
            System.out.println(item.newText + " " + this.toHex(va));
        }


        File file = new File(fileName);
        FileChannel channel = null;
        try {
            channel = new FileOutputStream(file, false).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }

        try {
            buf.position(0);
            channel.write(buf);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        return 1;

    }

    public int getStringsCount() {
        return strings.size();
    }

    public void setStrictFilterMode(boolean isStrict) {
        isStrictFilter = isStrict;
    }

}
