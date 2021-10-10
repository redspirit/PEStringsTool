package com.pestrings.pestringstool;

public class PEStringItem {

    public int offset;
    public String data;

    public PEStringItem(int offset, String data) {
        this.offset = offset;
        this.data = data;
    }

    public String toString() {
        return this.data;
    }

}
