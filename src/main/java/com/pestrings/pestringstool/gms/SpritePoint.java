package com.pestrings.pestringstool.gms;

public class SpritePoint {
    public short x, y;
    public SpritePoint(short x, short y) {
        this.x = x;
        this.y = y;
    }
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
