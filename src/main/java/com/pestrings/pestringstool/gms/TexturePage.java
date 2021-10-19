package com.pestrings.pestringstool.gms;

public class TexturePage {

    public SpriteRect source, target;
    public SpritePoint size;
    public short index;

    public TexturePage(SpriteRect source, SpriteRect target, SpritePoint size, short index) {
        this.source = source;
        this.target = target;
        this.size = size;
        this.index = index;
    }

    public String toString() {
        return index + " " + source + " " + target + " " + size;
    }
}
