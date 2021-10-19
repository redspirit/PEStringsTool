package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkFONT {

    private List<FontItem> fonts = new ArrayList<>();
    private List<Integer> addresses = new ArrayList<>();
    private int entries = 0;

    public ChunkFONT(ByteBuffer buffer, DataChunk chunk) {

        buffer.position(chunk.startAddress);

        entries = buffer.getInt(); // кол-во шрифтов

        for(int i = 0; i < entries; i++) {
            addresses.add(buffer.getInt());
        }

        for(int i = 0; i < entries; i++) {
            fonts.add(new FontItem(buffer, addresses.get(i)));
        }

    }

    public FontItem getFontByAddress(int address) {
        int index = addresses.indexOf(address);
        if(index == -1) return null;
        return fonts.get(index);
    };

    public FontItem getFontByIndex(int index) {
        return fonts.get(index);
    };

}
