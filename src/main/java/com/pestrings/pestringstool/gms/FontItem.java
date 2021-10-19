package com.pestrings.pestringstool.gms;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FontItem {

    public String name, fontName;
    public float size;
    public boolean isBold, isItalic;
    public int rangeBegin, rangeEnd;
    public int pagePointer;
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
        pagePointer = buffer.getInt();
        scaleX = buffer.getFloat();
        scaleY = buffer.getFloat();
        unknownData = buffer.getInt();
        charsCount = buffer.getInt();

        List<Integer> pointers = new ArrayList<>();

        // parse chars table
        for(int i = 0; i < charsCount; i++) {
            pointers.add(buffer.getInt());
        }

        for(int i = 0; i < charsCount; i++) {

            buffer.position(pointers.get(i));

            FontCharItem ch = new FontCharItem();
            ch.code = buffer.getShort() & 0xffff; // unsigned short
            ch.letter = Character.toString(ch.code);
            ch.posX = buffer.getShort();
            ch.posY = buffer.getShort();
            ch.sizeX = buffer.getShort();
            ch.sizeY = buffer.getShort();
            ch.shift = buffer.getShort();
            ch.offset = buffer.getInt();

            chars.add(ch);

        }

    }

    public Image getSprite() {
        TexturePage page = GMSDATA.tpag.getByAddress(pagePointer);
        return page.getImage();
    }

    public TexturePage getTexturePage() {
        return GMSDATA.tpag.getByAddress(pagePointer);
    }

}
