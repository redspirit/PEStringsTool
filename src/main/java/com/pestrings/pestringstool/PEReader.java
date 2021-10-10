package com.pestrings.pestringstool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public class PEReader {

    // convert hex to int
    private long toInt(String hex) {
        return Long.parseLong(hex, 16);
    }

    // convert int to hex
    private String toHex(long val) {
        return Long.toHexString(val);
    }


    public void loadFile(String path) {

        byte[] bytes = new byte[0];
        try {
            // todo проверить на неверном файле
            bytes = Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int ind = 2;
        System.out.println("Byte: " + buffer.get(ind)); // 1 byte
        System.out.println("Short: " + this.toHex(buffer.getShort(ind))); // 2 bytes
        System.out.println("Int: " + this.toHex(buffer.getInt(ind))); // 4 bytes
        System.out.println("Long: " + this.toHex(buffer.getLong(ind))); // 8 bytes


//
    }


}
