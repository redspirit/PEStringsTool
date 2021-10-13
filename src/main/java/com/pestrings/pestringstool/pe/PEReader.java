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
import java.util.stream.Collectors;

public class PEReader {

    private ByteBuffer buffer;
    private List<PEStringItem> strings = new ArrayList<>();
    public PEHeader headers = new PEHeader();
    private int fileSize = 0;

    // convert hex to int
    private long toInt(String hex) {
        return Long.parseLong(hex, 16);
    }

    // convert int to hex
    private String toHex(long val) {
        return Long.toHexString(val);
    }

    public boolean loadFile(String path) {

        byte[] bytes = new byte[0];
        try {
            // todo проверить на неверном файле
            bytes = Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        this.fileSize = bytes.length;
        this.buffer = ByteBuffer.wrap(bytes);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.headers.loadBuffer(this.buffer);

        if(!this.headers.signature.equals("PE")) return false;

        this.extractStrings();

        return true;

    }

    public void extractStrings() {

        int startOfSections = this.headers.sections.get(0).pointerToRawData;
        StringBuilder str = new StringBuilder();
        byte[] unicodeChar = new byte[2];
        int startOffset = 0;
        boolean isUtf8Old = false;

        for (int i = startOfSections; i < this.fileSize - 2; i++) {

            byte ch = this.buffer.get(i); // for latin
            buffer.slice(i, 2).get(unicodeChar);
            String uft8Char = new String(unicodeChar, StandardCharsets.UTF_8); // for utf8 1
            buffer.slice(i+1, 2).get(unicodeChar);
            String uft8Char2 = new String(unicodeChar, StandardCharsets.UTF_8); // for utf8 2
            boolean isAscii = (ch >= 32 && ch <= 126);
            boolean isUtf81 = Character.isLetter(uft8Char.charAt(0)) && uft8Char.length() == 1;
            boolean isUtf82 = Character.isLetter(uft8Char2.charAt(0)) && uft8Char2.length() == 1;



//            if(isLatter && uft8Char.length() == 1) {
//                System.out.println(uft8Char);
//            }

            if(!isUtf82 && ch == 0) {
                str = new StringBuilder();
            }

            if(isAscii) {
                str.append((char) ch);
//                System.out.println(str);
            } else if(isUtf81 || isUtf82) {

                if(isUtf81 && !isUtf82) {
                    str.append(uft8Char.charAt(0));
                }

//                if(ch == 0) System.out.println(str);

            } else if(ch == 0){

                 if(str.length() > 2) {
                    System.out.println("+++ " + str);
                    this.strings.add(new PEStringItem(startOffset, str.toString()));
                 }

                 str = new StringBuilder();

                 startOffset = i;
//                 System.out.println("CLEAR");
            }




//            if((ch >= 32 && ch <= 126) || isLatter) {
//                str.append(isLatter ? uft8Char.charAt(0) : (char) ch);
//                System.out.println(str);
//            } else if(str.length() > 0) {
//                if(str.length() > 2) {
//                    System.out.println("+++ " + str);
//                    this.strings.add(new PEStringItem(startOffset, str.toString()));
//                }
//                str = new StringBuilder();
//                startOffset = i;
//            }

        }

    }

    public List<PEStringItem> searchTexts (String text) {
        String txt = text.toLowerCase();
        if(text.equals("")) return this.strings;
        return this.strings.stream()
                .filter(item -> item.data.toLowerCase().contains(txt))
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

        for (PEReplaceItem item : items) {
            item.bytes = item.newText.getBytes(StandardCharsets.UTF_8);
            item.localAddr = textsLen;
            textsLen += (item.bytes.length + 1);
        }

        int secAlignment = this.headers.sectionAlignment;
        int contentVirtLen = (int) Math.ceil(textsLen / 64.0) * 64;
        int contentLen = (int) Math.ceil(textsLen / 512.0) * 512;
        ByteBuffer content = ByteBuffer.allocate(contentLen);
        content.order(ByteOrder.LITTLE_ENDIAN);

        for (PEReplaceItem item : items) {
            content.put(item.localAddr, item.bytes);
        }

        ByteBuffer buf = ByteBuffer.allocate(this.buffer.capacity() + content.capacity());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(this.buffer);

        int peHeaderAddress = this.headers.e_lfanew;
        PESection lastSection = this.headers.sections.get(this.headers.sections.size() - 1);
        int headerAddr = lastSection.headerPointer + 40;

        int sectionsAddr = this.buffer.capacity();
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
//            System.out.println(item.newText + " " + this.toHex(va));
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

}
