package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;

public class FontItem {

    public String fileName;
    public String name;

    public FontItem(ByteBuffer buffer, int start) {

        buffer.position(start);

        fileName = GMSDATA.strg.getStringByAddress(buffer.getInt());
        name = GMSDATA.strg.getStringByAddress(buffer.getInt());

        System.out.println(start + " " + name );



    }
}
