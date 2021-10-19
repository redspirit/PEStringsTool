package com.pestrings.pestringstool.gms;

public class SpriteRect {
    public SpritePoint position, size;

    public SpriteRect(SpritePoint position, SpritePoint size) {
        this.position = position;
        this.size = size;
    }
    public String toString() {
        return "[" + position + "," + size + "]";
    }
}
