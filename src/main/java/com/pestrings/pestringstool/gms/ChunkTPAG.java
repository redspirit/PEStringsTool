package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkTPAG {

    private List<Integer> addresses = new ArrayList<>();
    private List<TexturePage> pages = new ArrayList<>();
    public int entries = 0;

    public ChunkTPAG(ByteBuffer buffer, DataChunk chunk) {

        buffer.position(chunk.startAddress);

        entries = buffer.getInt();

        for(int i = 0; i < entries; i++) {
            addresses.add(buffer.getInt());
        }

        for(int i = 0; i < entries; i++) {
            buffer.position(addresses.get(i));

            SpriteRect source = new SpriteRect(
                    new SpritePoint(buffer.getShort(), buffer.getShort()),
                    new SpritePoint(buffer.getShort(), buffer.getShort())
            );
            SpriteRect target = new SpriteRect(
                    new SpritePoint(buffer.getShort(), buffer.getShort()),
                    new SpritePoint(buffer.getShort(), buffer.getShort())
            );
            SpritePoint size = new SpritePoint(buffer.getShort(), buffer.getShort());
            short index = buffer.getShort();

            pages.add(new TexturePage(source, target, size, index));

        }

    }

    public TexturePage getByAddress(int address) {
        int index = addresses.indexOf(address);
        if(index == -1) return null;
        return pages.get(index);
    }

}
