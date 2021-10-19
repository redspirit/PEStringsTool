package com.pestrings.pestringstool.gms;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class TexturePage {

    public SpriteRect source, target;
    public SpritePoint size;
    public short textureIndex;

    public TexturePage(SpriteRect source, SpriteRect target, SpritePoint size, short textureIndex) {
        this.source = source;
        this.target = target;
        this.size = size;
        this.textureIndex = textureIndex; // texture index
    }

    public Image getImage() {
        Texture txt = GMSDATA.txtr.getByIndex(textureIndex);
        PixelReader reader = txt.getImage().getPixelReader();
        return new WritableImage(reader, source.position.x, source.position.y, source.size.x, source.size.y);
    }

    public String toString() {
        return textureIndex + " " + source + " " + target + " " + size;
    }
}
