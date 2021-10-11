package com.pestrings.pestringstool;

import java.util.Locale;

public class PEReplaceItem {

    public PEStringItem stringItem;
    public String newText;
    public byte[] bytes;
    public int localAddr;

    public PEReplaceItem(PEStringItem stringItem, String newText) {
        this.stringItem = stringItem;
        this.newText = newText;
    }

    public String toString() {;
        return "> " + this.stringItem.data + "\n= " + this.newText;
    }

}
