package com.pestrings.pestringstool;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
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
    public ListView<PEStringItem> stringsList;
    public TextField searchBox;

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

            stringsList.getItems().setAll(peReader.searchTexts(""));

        }




    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    public void setReader(PEReader reader) {
        peReader = reader;
    }


    public void onDoSearchString(KeyEvent keyEvent) {

        stringsList.getItems().setAll( peReader.searchTexts(searchBox.getText()) );

    }
}