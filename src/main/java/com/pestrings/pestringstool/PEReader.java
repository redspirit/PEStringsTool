package com.pestrings.pestringstool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public class PEReader {

    private ByteBuffer buffer;

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

        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        PEHeader headers = new PEHeader();
        headers.loadBuffer(buffer);

        
//        System.out.println(headers.sections);


    }

    public void getHeaders() {



    }

}
