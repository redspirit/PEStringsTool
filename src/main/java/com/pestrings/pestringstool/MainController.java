package com.pestrings.pestringstool;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    public ListView<PEReplaceItem> replacesList;
    public TextField searchBox;
    public TextArea originalTextView;
    public TextArea newTextView;
    public Label statusTextView;
    public Button saveButtonView;

    public PEStringItem currentString = null;

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

            boolean result = peReader.loadFile(file.getPath());
            if(!result) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong PE file!", ButtonType.CLOSE);
                alert.showAndWait();
            } else {
                // load ok
                stringsList.getItems().setAll(peReader.searchTexts(""));
            }

        }

    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    public void setReader(PEReader reader) {
        peReader = reader;
    }

    // набираем текст в форме фильтра
    public void onDoSearchString(KeyEvent keyEvent) {
        stringsList.getItems().setAll( peReader.searchTexts(searchBox.getText()) );
    }

    // кликнули по выбранному элементу списка строк
    public void onClickToStringsList(MouseEvent mouseEvent) {

        ObservableList<PEStringItem> items = stringsList.getSelectionModel().getSelectedItems();

        if(items.size() > 0) {
            PEStringItem item = items.get(0);
            originalTextView.setText(item.data);
            currentString = item;
            onTextsViewTyped(null);
        }

    }

    public void onSaveText(ActionEvent actionEvent) {

        PEReplaceItem item = new PEReplaceItem(this.currentString, newTextView.getText());
        replacesList.getItems().add(item);

    }

    public void onTextsViewTyped(KeyEvent keyEvent) {

        boolean dis = newTextView.getText().equals("") || this.currentString == null;

        saveButtonView.setDisable(dis);

    }
}