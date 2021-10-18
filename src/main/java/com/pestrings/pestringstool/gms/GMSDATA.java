package com.pestrings.pestringstool.gms;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GMSDATA {

    private static ByteBuffer buffer;
    private static int fileSize = 0;

    private static List<String> names = new ArrayList<>();
    private static List<DataChunk> chunks = new ArrayList<>();

    public static ChunkSTRG strg;
    public static ChunkFONT font;

    static public boolean loadFile(String path) {

        byte[] bytes;
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

        /// start parsing chunks
        buffer.position(0);
        int i = 8;

        while (i < fileSize - 1) {

            byte[] nameBytes = new byte[4];
            buffer.slice(i, 4).get(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);
            i += 4;

            int chunkLen = buffer.getInt(i);
//            System.out.println(name + " = " + chunkLen);

            names.add(name);
            chunks.add( new DataChunk(name, i + 4, chunkLen) );

            i = chunkLen + i + 4;

        }

        strg = new ChunkSTRG(buffer, getChunkAddress("STRG"));
//        System.out.println("STR " + strg.getStringByAddress(8984380) );

        font = new ChunkFONT(buffer, getChunkAddress("FONT"));

        return true;

    }


    static public DataChunk getChunkAddress(String name) {
        return chunks.get(names.indexOf(name));
    }

}
