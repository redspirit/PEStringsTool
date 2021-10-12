package com.pestrings.pestringstool;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class MainController {

    public Stage primaryStage;
    public PEReader peReader;
    public ProjectManager project = new ProjectManager();
    public ListView<PEStringItem> stringsList;
    public ListView<PEReplaceItem> replacesList;
    public TextField searchBox;
    public TextArea originalTextView;
    public TextArea newTextView;
    public Label statusTextView;
    public Button saveButtonView;

    public PEStringItem currentString = null;

    public void onExit(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure? Unsaved data will be lost");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
            //don't close stage
        }
        Platform.exit();
//        System.exit(0);
    }

    public void onOpenExe(ActionEvent actionEvent) throws IOException {

        FileChooser fc = new FileChooser();
        fc.setTitle("Open EXE file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE files (*.exe)", "*.exe"));
        File file = fc.showOpenDialog(primaryStage);
        if (file != null) {

            peReader = new PEReader();
            project.reset(file.getPath());

            boolean result = peReader.loadFile(file.getPath());
            if(!result) {
                new Alert(Alert.AlertType.ERROR, "Wrong PE file! Select the executable file for Windows", ButtonType.CLOSE).showAndWait();
            } else {
                // load ok

                PESection sec = peReader.headers.getSectionByName(".pestool");
                if(sec == null) {
                    // ok
                    stringsList.getItems().setAll(peReader.searchTexts(""));
                } else {
                    new Alert(Alert.AlertType.ERROR, "This file has already been modified. Select the file without modification", ButtonType.CLOSE).showAndWait();
                }

            }

        }

    }

    public void setStage(Stage stage) {
        primaryStage = stage;
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
            newTextView.requestFocus();
        }

    }

    // сохраняем перевод в отдельный список
    public void onSaveText(ActionEvent actionEvent) {

        if(currentString == null) return;

        replacesList.getItems().add(new PEReplaceItem(currentString, newTextView.getText()));

        newTextView.setText("");
        originalTextView.setText("");
        currentString = null;
        onTextsViewTyped(null);

    }

    public void onTextsViewTyped(KeyEvent keyEvent) {

        boolean dis = newTextView.getText().equals("") || this.currentString == null;
        saveButtonView.setDisable(dis);

    }

    public void onSaveMod(ActionEvent actionEvent) {

        if(replacesList.getItems().size() == 0) {
            new Alert(Alert.AlertType.WARNING, "No data to save", ButtonType.CLOSE).showAndWait();
            return;
        };

        FileChooser fc = new FileChooser();
        fc.setTitle("Save EXE file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE files (*.exe)", "*.exe"));
        File file = fc.showSaveDialog(primaryStage);

        if (file != null) {
            // save new file
            int result = peReader.applyChanges(replacesList.getItems(), file.getPath());
            if (result == 1) {
                new Alert(Alert.AlertType.INFORMATION, "File modified successfully", ButtonType.OK).showAndWait();
            }
            if(result == 0) {
                new Alert(Alert.AlertType.ERROR, "Can't save file", ButtonType.CLOSE).showAndWait();
            }

        }


    }

    public void onClickAbout(ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        FXMLLoader fxmlAbout = new FXMLLoader(Application.class.getResource("about-view.fxml"));
        Scene scene = new Scene(fxmlAbout.load(), 400, 238);
        stage.setScene(scene);
        stage.setTitle("About PEStringsTool");
        stage.setResizable(false);
        stage.show();

    }

    public void onOpenProject(ActionEvent actionEvent) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Open project file (.pes)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project files (*.pes)", "*.pes"));
        File file = fc.showOpenDialog(primaryStage);
        if (file != null) {

            String exePath = "";

            try {
                exePath = project.loadFile(replacesList.getItems(), file.getPath());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                return;
            }

            peReader = new PEReader();
            boolean result = peReader.loadFile(exePath);
            stringsList.getItems().setAll(peReader.searchTexts(""));


        }

    }

    public void onSaveProject(ActionEvent actionEvent) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Save project file (.pes)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project files (*.pes)", "*.exe"));
        File file = fc.showSaveDialog(primaryStage);

        if (file != null) {

            project.saveFile(replacesList.getItems(), file.getPath());

        }

    }

    public void onSaveProjectAs(ActionEvent actionEvent) {


    }
}