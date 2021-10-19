package com.pestrings.pestringstool;

import com.pestrings.pestringstool.gms.FontItem;
import com.pestrings.pestringstool.gms.GMSDATA;
import com.pestrings.pestringstool.gms.Texture;
import com.pestrings.pestringstool.gms.TexturePage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class FontController {

    public ImageView imageView;

    public void ViewLoaded() {

        FontItem font = GMSDATA.font.getFontByIndex(1);

        Image img = font.getSprite();

        imageView.setSmooth(false);
        imageView.setFitHeight(img.getHeight() * 2);
        imageView.setFitWidth(img.getHeight() * 2);

        System.out.println(font.name);

        imageView.setImage(img);

    }

}
