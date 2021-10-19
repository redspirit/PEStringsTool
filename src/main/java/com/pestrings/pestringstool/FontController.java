package com.pestrings.pestringstool;

import com.pestrings.pestringstool.gms.FontCharItem;
import com.pestrings.pestringstool.gms.FontItem;
import com.pestrings.pestringstool.gms.GMSDATA;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

public class FontController {

    public Canvas canvasView;
    public TextField fontIndexView;

    public void ViewLoaded() {

        showFont(0);

    }

    public void showFont(int index) {

        FontItem font = GMSDATA.font.getFontByIndex(index);

        Image img = font.getSprite();
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

//        System.out.println(font.getTexturePage());

        canvasView.setWidth(w);
        canvasView.setHeight(h);
        GraphicsContext ctx = canvasView.getGraphicsContext2D();

        ctx.setImageSmoothing(false);
        ctx.setFill(Paint.valueOf("#00587a"));
        ctx.fillRect(0,0, w, h);
        ctx.drawImage(img, 0, 0, w, h);

        // draw symbols
        ctx.setStroke(Paint.valueOf("#ff0000"));
        for(FontCharItem ch : font.chars) {

            ctx.strokeRect(ch.posX, ch.posY, ch.sizeX, ch.sizeY);
            
        }


    }

    public void onSelectFont(ActionEvent actionEvent) {

        int index = Integer.parseInt(fontIndexView.getText());

        showFont(index);

    }
}
