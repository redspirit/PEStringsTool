package com.pestrings.pestringstool.gms;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Texture {

    public int scaled, generatedMips;
    public int pngPointer;
    private byte[] imageBytes;

    private final long endNumber;

    public Texture() {
        byte[] endBytes = new byte[]{0x49,0x45,0x4E,0x44,(byte)0xAE,0x42,0x60,(byte)0x82};
        ByteBuffer bb = ByteBuffer.wrap(endBytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        endNumber = bb.getLong(0);
    }

    public void loadPng(ByteBuffer buffer) {
        int i = 64;
        while(buffer.getLong(pngPointer + i) != endNumber) {
            i++;
        }
        imageBytes = new byte[i + 8];
        buffer.slice(pngPointer, i + 8).get(imageBytes);
    }

    public Image getImage() {
        Image image = new Image(new ByteArrayInputStream(imageBytes));
        return image;
    }

    public String toString() {
        return "scaled="+scaled+" generatedMips="+generatedMips+" png="+pngPointer;
    }

}
