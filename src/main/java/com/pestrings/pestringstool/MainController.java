package com.pestrings.pestringstool;

import com.pestrings.pestringstool.pe.PEReader;
import com.pestrings.pestringstool.pe.PEReplaceItem;
import com.pestrings.pestringstool.pe.PESection;
import com.pestrings.pestringstool.pe.PEStringItem;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    public Stage stage;
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
    public CheckBox cbSearchEqual;
    public CheckBox cbSearchCase;
    public HostServices hostServices;
    public CheckMenuItem isUseStrictFilter;


    public void onExit(ActionEvent actionEvent) {

        Optional<ButtonType> result =new Alert(Alert.AlertType.CONFIRMATION, "Are you sure? Unsaved data will be lost").showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
            //don't close stage
        }
        Platform.exit();
//        System.exit(0);
    }

    public void loadExe(String path, boolean isProjectLoad) {

        peReader = new PEReader();
        project.reset(path);

        statusTextView.setText("Loading...");

        boolean result = peReader.loadFile(path);
        if(!result) {
            statusTextView.setText("Wrong PE file!");
            new Alert(Alert.AlertType.ERROR, "Wrong PE file! Select the executable file for Windows", ButtonType.CLOSE).showAndWait();
        } else {
            // load ok

            PESection sec = peReader.headers.getSectionByName(".pestool");
            if(sec == null) {
                // ok
                if(isProjectLoad) {
                    List<PEReplaceItem> repList = replacesList.getItems();
                    peReader.strings = peReader.strings.stream().peek(item -> {

                        repList.stream().filter(rItem -> rItem.stringItem.offset == item.offset).findFirst().ifPresent(match -> item.setTranslated(true));

                    }).collect(Collectors.toList());
                } else {
                    replacesList.getItems().clear();
                }
                stringsList.getItems().setAll(peReader.searchTexts("", false, false));

                String filename = Paths.get(path).getFileName().toString();
                statusTextView.setText(filename + " loaded. Found " + peReader.getStringsCount() + " strings");
            } else {
                statusTextView.setText("This file has already been modified");
                new Alert(Alert.AlertType.ERROR, "This file has already been modified. Select the file without modification", ButtonType.CLOSE).showAndWait();
            }

        }

    }

    public void onOpenExe(ActionEvent actionEvent) throws IOException {

        FileChooser fc = new FileChooser();
        fc.setTitle("Open EXE file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE files (*.exe)", "*.exe"));
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            loadExe(file.getPath(), false);
        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // набираем текст в форме фильтра
    public void onDoSearchString(KeyEvent keyEvent) {
        stringsList.getItems().setAll( peReader.searchTexts(searchBox.getText(), cbSearchEqual.isSelected(), cbSearchCase.isSelected() ));
    }

    // кликнули по выбранному элементу списка строк
    public void onClickToStringsList(MouseEvent mouseEvent) {

        ObservableList<PEStringItem> items = stringsList.getSelectionModel().getSelectedItems();

        if(items.size() > 0) {
            PEStringItem item = items.get(0);

            PESection sect = peReader.headers.getSectionByOffset(item.offset);
            statusTextView.setText("Selected string at 0x" + peReader.toHex(item.offset) + sect.name);

            // find translation
            if(item.isTranslated) {
                replacesList.getItems().stream().filter(rItem -> rItem.stringItem.offset == item.offset).findFirst().ifPresent(match -> newTextView.setText(match.newText));
            }

            originalTextView.setText(item.data);
            currentString = item;
            onTextsViewTyped(null);
            newTextView.requestFocus();
        }

    }

    // сохраняем перевод в отдельный список
    public void onSaveText(ActionEvent actionEvent) {

        if(currentString == null) return;

        PEReplaceItem matched = replacesList.getItems().stream().filter(item -> item.stringItem.offset == currentString.offset).findFirst().orElse(null);
        if(matched == null) {
            // add
            currentString.setTranslated(true);
            replacesList.getItems().add(new PEReplaceItem(currentString, newTextView.getText()));
            replacesList.refresh();
        } else {
            // update
            int index = replacesList.getItems().indexOf(matched);
            matched.newText = newTextView.getText();
            replacesList.getItems().set(index, matched);
            replacesList.refresh();
        }

        int replaceCount = replacesList.getItems().size();
        statusTextView.setText("Added translations: " + replaceCount);

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
        File file = fc.showSaveDialog(stage);

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
        AboutController ctrl = fxmlAbout.getController();
        ctrl.setHostServices(hostServices);
        stage.show();

    }

    public void onOpenProject(ActionEvent actionEvent) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Open project file (.pes)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project files (*.pes)", "*.pes"));
        File file = fc.showOpenDialog(stage);
        if (file != null) {

            try {

                replacesList.getItems().addAll(
                        project.loadFile(file.getPath())
                );

            } catch (IOException | ParseException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to load project file. File read error.", ButtonType.CLOSE).showAndWait();
                return;
            }

            loadExe(project.exePath, true);

            // .collect(Collectors.toList())

        }

    }

    public void onSaveProject(ActionEvent actionEvent) {

        if(project.exePath.equals("") || replacesList.getItems().size() == 0) {
            new Alert(Alert.AlertType.INFORMATION, "Nothing to save", ButtonType.CLOSE).showAndWait();
            return;
        }

        if(project.projectPath.equals("")) {
            onSaveProjectAs(actionEvent);
        } else {
            project.saveFile(replacesList.getItems(), project.projectPath);
            new Alert(Alert.AlertType.INFORMATION, "Project saved", ButtonType.OK).showAndWait();
        }

    }

    public void onSaveProjectAs(ActionEvent actionEvent) {

        if(project.exePath.equals("") || replacesList.getItems().size() == 0) {
            new Alert(Alert.AlertType.INFORMATION, "Nothing to save", ButtonType.CLOSE).showAndWait();
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Save project file (.pes)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project files (*.pes)", "*.pes"));
        File file = fc.showSaveDialog(stage);

        if (file != null) {
            project.saveFile(replacesList.getItems(), file.getPath());
            new Alert(Alert.AlertType.INFORMATION, "Project saved", ButtonType.OK).showAndWait();
        }

    }

    public void doSelectReplaceItem(PEReplaceItem item) {

        originalTextView.setText(item.stringItem.data);
        newTextView.setText(item.newText);
        currentString = item.stringItem;
        onTextsViewTyped(null);

        statusTextView.setText("Edit translation...");

    }

    public void addContextMenuOnList() {


        replacesList.setCellFactory(lv -> {

            ListCell<PEReplaceItem> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> {
                if(cell.getItem() != null) doSelectReplaceItem(cell.getItem());
            });
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                cell.getItem().stringItem.setTranslated(false);
                replacesList.getItems().remove(cell.getItem());
            });
            contextMenu.getItems().addAll(editItem, deleteItem);

            cell.setContextMenu(contextMenu);

            cell.textProperty().bind(Bindings.createStringBinding(
                () -> Objects.toString(cell.getItem(), ""),
                cell.itemProperty()
            ));

            return cell;
        });

    }

    public void onCbSearchOptsSelect(ActionEvent e) {
        if(peReader != null) {
            stringsList.getItems().setAll( peReader.searchTexts(searchBox.getText(), cbSearchEqual.isSelected(), cbSearchCase.isSelected() ));
        }
    }

    public void setHostServices(HostServices hs) {
        hostServices = hs;
    }

    public void onOpenIssues(ActionEvent actionEvent) {
        hostServices.showDocument("https://github.com/redspirit/PEStringsTool/issues");
    }

    public void onUserStrictFilter(ActionEvent actionEvent) {
        if(peReader != null) {
            peReader.setStrictFilterMode(isUseStrictFilter.isSelected());
            stringsList.getItems().setAll( peReader.searchTexts(searchBox.getText(), cbSearchEqual.isSelected(), cbSearchCase.isSelected() ));
        }
    }

    public void onFontEditorMenu(ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        FXMLLoader fxmlFont = new FXMLLoader(Application.class.getResource("font-view.fxml"));
        Scene scene = new Scene(fxmlFont.load(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Font editor");
        stage.setResizable(true);
        FontController ctrl = fxmlFont.getController();
        ctrl.ViewLoaded();
//        ctrl.setHostServices(hostServices);
        stage.show();

    }
}