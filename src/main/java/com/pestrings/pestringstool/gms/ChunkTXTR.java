package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkTXTR {

    private List<Integer> addresses = new ArrayList<>();
    public List<Texture> textures = new ArrayList<>();
    public int entries = 0;

    public ChunkTXTR(ByteBuffer buffer, DataChunk chunk) {

        buffer.position(chunk.startAddress);

        entries = buffer.getInt();

        for(int i = 0; i < entries; i++) {
            addresses.add(buffer.getInt());
        }

        for(int i = 0; i < entries; i++) {
            buffer.position(addresses.get(i));

            Texture t = new Texture();
            t.scaled = buffer.getInt();
            t.generatedMips = buffer.getInt();
            t.pngPointer = buffer.getInt();
            t.loadPng(buffer);
            textures.add(t);

        }

    }

    public Texture getByAddress(int address) {
        int index = addresses.indexOf(address);
        if(index == -1) return null;
        return textures.get(index);
    }

    public Texture getByIndex(int index) {
        return textures.get(index);
    }

}
