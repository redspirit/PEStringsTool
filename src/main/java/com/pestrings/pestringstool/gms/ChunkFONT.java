package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChunkFONT {

    private List<String> values = new ArrayList<>();
    private List<Integer> addresses = new ArrayList<>();
    private int entries = 0;

    public ChunkFONT(ByteBuffer buffer, DataChunk chunk) {

        buffer.position(chunk.startAddress);

        entries = buffer.getInt(); // кол-во шрифтов

        for(int i = 0; i < entries; i++) {
            addresses.add(buffer.getInt());     // массив адресов начала шрифтов
        }

        for(int i = 0; i < entries; i++) {
            new FontItem(buffer, addresses.get(i));
        }

    }

}
