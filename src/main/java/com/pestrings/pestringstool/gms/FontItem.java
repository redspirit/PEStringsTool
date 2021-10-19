package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FontItem {

    public String name, fontName;
    public float size;
    public boolean isBold, isItalic;
    public int rangeBegin, rangeEnd;
    public int texturePointer;
    public float scaleX, scaleY;
    public int charsCount;
    public List<FontCharItem> chars = new ArrayList<>();

    public FontItem(ByteBuffer buffer, int start) {

        buffer.position(start);

        int unknownData = 0;

        name = GMSDATA.strg.getStringByAddress(buffer.getInt());
        fontName = GMSDATA.strg.getStringByAddress(buffer.getInt());
        size = buffer.getFloat();
        isBold = buffer.getInt() > 0;
        isItalic = buffer.getInt() > 0;
        rangeBegin = buffer.getInt();
        rangeEnd = buffer.getInt();
        texturePointer = buffer.getInt();
        scaleX = buffer.getFloat();
        scaleY = buffer.getFloat();
        unknownData = buffer.getInt();
        charsCount = buffer.getInt();

        System.out.println(start + " " + name + " " + texturePointer);

        List<Integer> pointers = new ArrayList<>();

        if(name.equals("picoscript123")) {

//            System.out.println("GO!");

            for(int i = 0; i < charsCount; i++) {
                pointers.add(buffer.getInt());
            }

            for(int i = 0; i < charsCount; i++) {

                buffer.position(pointers.get(i));

                FontCharItem ch = new FontCharItem();

                ch.code = buffer.getShort();
                ch.letter = Character.toString(ch.code);
                ch.posX = buffer.getShort();
                ch.posY = buffer.getShort();
                ch.sizeX = buffer.getShort();
                ch.sizeY = buffer.getShort();
                ch.shift = buffer.getShort();
                ch.offset = buffer.getInt();

                chars.add(ch);

                System.out.println(ch.code + " " + ch.letter);

            }

        }


    }
}
