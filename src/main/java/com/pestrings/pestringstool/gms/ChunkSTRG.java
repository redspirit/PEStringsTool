package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChunkSTRG {

    private List<String> values = new ArrayList<>();
    private List<Integer> addresses = new ArrayList<>();
    public int entries = 0;

    public ChunkSTRG(ByteBuffer buffer, DataChunk chunk) {

        buffer.position(chunk.startAddress);

        entries = buffer.getInt(); // кол-во строк

        for(int i = 0; i < entries; i++) {
            addresses.add(buffer.getInt());     // массив адресов начала строк
        }

        for(int i = 0; i < entries; i++) {

            buffer.position(addresses.get(i));      // для каждой строки берем адрес ее начала
            int length = buffer.getInt();           // и от туда читаем длину строки

            byte[] chars = new byte[length];
            buffer.get(chars);                     // читаем из буфера символы указанной длины
            String s = new String(chars, StandardCharsets.UTF_8);
            values.add(s);

        }

    }

    public String getStringByAddress(int address) {
        int index = addresses.indexOf(address - 4);
        if(index == -1) return "[not_found]";
        return values.get(index);
    }

}
