package com.pestrings.pestringstool.gms;

import java.nio.ByteBuffer;

public class Texture {

    public int scaled, generatedMips;
    public int pngPointer;
    public ByteBuffer png;

    public Texture() {}

    public Texture(int scaled, int generatedMips, int pngPointer) {
        this.scaled = scaled;
        this.generatedMips = generatedMips;
        this.pngPointer = pngPointer;
    }

    public void loadPng(ByteBuffer buffer) {
        // надо взять основной буфер, найти в нем от конц картинки и вырезать этот кусов отдельный буфер
        // end bytes = 49 45 4E 44 AE 42 60 82
    }

    public String toString() {
        return "scaled="+scaled+" generatedMips="+generatedMips+" png="+pngPointer;
    }

}
