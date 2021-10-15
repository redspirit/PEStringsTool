package com.pestrings.pestringstool.pe;

public class PEStringItem {

    public int offset;
    public String data;
    public boolean isTranslated = false;

    public PEStringItem(int offset, String data) {
        this.offset = offset;
        this.data = data;
    }

    public String toString() {
        String prefix = isTranslated ? "[✔️]" : "";
        return prefix + this.data;
    }

    public void setTranslated(boolean translated) {
        isTranslated = translated;
    }

}
