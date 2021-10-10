package com.pestrings.pestringstool;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainController {

    public Stage primaryStage;
    public PEReader peReader;

    public void onExit(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void onOpenExe(ActionEvent actionEvent) throws IOException {

        FileChooser fc = new FileChooser();

        fc.setTitle("Open EXE file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE files (*.exe)", "*.exe"));
        File file = fc.showOpenDialog(primaryStage);
        if (file != null) {
//            fileLabel.setText(file.getPath());

            peReader.loadFile(file.getPath());

//            char singleChar;
//            for(byte b : bytes) {
//                singleChar = (char) b;
//                System.out.println(b);
//            }

        }

    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    public void setReader(PEReader reader) {
        peReader = reader;
    }
}