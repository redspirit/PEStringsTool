package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChunkStrg {

    public List<String> values = new ArrayList<>();

    public ChunkStrg(ByteBuffer buffer, int start) {

        buffer.position(start);

        int entries = buffer.getInt();

        List<Integer> addresses = new ArrayList<>();
        for(int i = 0; i < entries; i++) {
            addresses.add(buffer.getInt());
        }

        for(int i = 0; i < entries; i++) {

            buffer.position(addresses.get(i));
            int length = buffer.getInt();

            byte[] chars = new byte[length];
            buffer.get(chars);
            String s = new String(chars, StandardCharsets.UTF_8);
            values.add(s);

        }

    }

}
