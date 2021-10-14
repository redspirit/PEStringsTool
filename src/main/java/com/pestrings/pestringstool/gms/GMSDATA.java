package com.pestrings.pestringstool.gms;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GMSDATA {

    private ByteBuffer buffer;
    private int fileSize = 0;

    public ChunkGen8 chunkGen8;
    public ChunkStrg chunkStrg;


    public boolean loadFIle(String path) {

        byte[] bytes = new byte[0];
        try {
            // todo проверить на неверном файле
            bytes = Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        fileSize = bytes.length;
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        readChunks();

        return true;

    }

    private void readChunks() {

        buffer.position(0);
        int i = 8;

        while (i < fileSize - 1) {

            byte[] nameBytes = new byte[4];
            buffer.slice(i, 4).get(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);
            i += 4;

            int chunkLen = buffer.getInt(i);
            System.out.println(name + " = " + chunkLen);
            if(name.equals("GEN8")) {
                chunkGen8 = new ChunkGen8(buffer, i + 4);
            }
            if(name.equals("STRG")) {
                chunkStrg = new ChunkStrg(buffer, i + 4);
            }

            i = chunkLen + i + 4;

        }

        System.out.println("END!");

    }

}
