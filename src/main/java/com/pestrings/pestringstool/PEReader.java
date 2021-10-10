package com.pestrings.pestringstool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PEReader {

    private ByteBuffer buffer;
    private List<PEStringItem> strings = new ArrayList<>();
    private PEHeader headers = new PEHeader();
    private int fileSize = 0;

    // convert hex to int
    private long toInt(String hex) {
        return Long.parseLong(hex, 16);
    }

    // convert int to hex
    private String toHex(long val) {
        return Long.toHexString(val);
    }

//    public String extractString(ByteBuffer buffer, int start, int length) {
//
//    }

    public void loadFile(String path) {

        byte[] bytes = new byte[0];
        try {
            // todo проверить на неверном файле
            bytes = Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.fileSize = bytes.length;
        this.buffer = ByteBuffer.wrap(bytes);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.headers.loadBuffer(this.buffer);

        this.extractStrings();


//        System.out.println(headers.sections);


    }

    public void extractStrings() {

        int startOfSections = this.headers.sections.get(0).pointerToRawData;

        StringBuilder str = new StringBuilder();

        for (int i = startOfSections; i < this.fileSize - startOfSections; i++) {

            byte ch = this.buffer.get(i);
            if(ch >= 32 && ch <= 126) {
                str.append((char) ch);
            } else if(str.length() > 0) {
                if(str.length() > 2) this.strings.add(new PEStringItem(i - str.length(), str.toString()));
                str = new StringBuilder();
            }

        }

//        for (PEStringItem item : this.strings) {
//            System.out.println(item.offset + " " + item.data);
//        }

    }

    public List<PEStringItem> searchTexts (String text) {
        String txt = text.toLowerCase();
        if(text.equals("")) return this.strings;
        return this.strings.stream()
                .filter(item -> item.data.toLowerCase().contains(txt))
                .collect(Collectors.toList());
    }

}
