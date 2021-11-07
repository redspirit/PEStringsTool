package com.pestrings.pestringstool;

import com.pestrings.pestringstool.exceptions.ExeNotFound;
import com.pestrings.pestringstool.pe.PEReader;
import com.pestrings.pestringstool.pe.PEReplaceItem;
import com.pestrings.pestringstool.pe.PESection;
import com.pestrings.pestringstool.pe.PEStringItem;
import com.pestrings.pestringstool.utils.AppSettings;
import com.pestrings.pestringstool.utils.NetUtils;
import com.pestrings.pestringstool.utils.Translate;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

    public Stage stage;
    public PEReader peReader;
    public ProjectManager project = new ProjectManager();
    public ListView<PEStringItem> stringsList;
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
    public TableView<PEReplaceItem> tableView = new TableView<>();
    public ObservableList<PEReplaceItem> replaceItems = FXCollections.observableArrayList();
    public TextField translatedSearchView;


    public void onExit(ActionEvent actionEvent) {

        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure? Unsaved data will be lost").showAndWait();
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
                    List<PEReplaceItem> repList = replaceItems;
                    peReader.strings = peReader.strings.stream().peek(item -> {

                        repList.stream().filter(rItem -> rItem.stringItem.offset == item.offset).findFirst().ifPresent(match -> item.setTranslated(true));

                    }).collect(Collectors.toList());
                } else {
                    replaceItems.clear();
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

    public void onLoaded(Stage stage) {
        this.stage = stage;
        addContextMenuOnList();
        createTableColumns();
    }

    // набираем текст в форме фильтра
    public void onDoSearchString(KeyEvent keyEvent) {
        if(peReader != null) {
            stringsList.getItems().setAll( peReader.searchTexts(searchBox.getText(), cbSearchEqual.isSelected(), cbSearchCase.isSelected() ));
        }
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
                replaceItems.stream().filter(rItem -> rItem.stringItem.offset == item.offset).findFirst().ifPresent(match -> newTextView.setText(match.newText));
            } else {
                newTextView.setText("");
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

        // todo recover
        PEReplaceItem matched = replaceItems.stream().filter(item -> item.stringItem.offset == currentString.offset).findFirst().orElse(null);
        if(matched == null) {
            // add
            currentString.setTranslated(true);
            replaceItems.add(new PEReplaceItem(currentString, newTextView.getText()));
            tableView.refresh();
        } else {
            // update
            int index = replaceItems.indexOf(matched);
            matched.newText = newTextView.getText();
            replaceItems.set(index, matched);
            tableView.refresh();
        }

        int replaceCount = replaceItems.size();
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

        if(replaceItems.size() == 0) {
            new Alert(Alert.AlertType.WARNING, "No data to save", ButtonType.CLOSE).showAndWait();
            return;
        };

        FileChooser fc = new FileChooser();
        fc.setTitle("Save EXE file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE files (*.exe)", "*.exe"));
        File file = fc.showSaveDialog(stage);

        if (file != null) {
            // save new file
            int result = peReader.applyChanges(replaceItems, file.getPath());
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
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.stage);
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

                replaceItems.addAll(
                        project.loadFile(file.getPath(), stage)
                );

            } catch (IOException | ParseException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to load project file. File read error.", ButtonType.CLOSE).showAndWait();
                return;
            } catch (ExeNotFound e) {
                new Alert(Alert.AlertType.ERROR, "Unable to load project file. EXE not found.", ButtonType.CLOSE).showAndWait();
                return;
            }

            loadExe(project.exePath, true);

        }

    }

    public void onSaveProject(ActionEvent actionEvent) {

        if(project.exePath.equals("") || replaceItems.size() == 0) {
            new Alert(Alert.AlertType.INFORMATION, "Nothing to save", ButtonType.CLOSE).showAndWait();
            return;
        }

        if(project.projectPath.equals("")) {
            onSaveProjectAs(actionEvent);
        } else {
            project.saveFile(replaceItems, project.projectPath);
            new Alert(Alert.AlertType.INFORMATION, "Project saved", ButtonType.OK).showAndWait();
        }

    }

    public void onSaveProjectAs(ActionEvent actionEvent) {

        if(project.exePath.equals("") || replaceItems.size() == 0) {
            new Alert(Alert.AlertType.INFORMATION, "Nothing to save", ButtonType.CLOSE).showAndWait();
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Save project file (.pes)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project files (*.pes)", "*.pes"));
        File file = fc.showSaveDialog(stage);

        if (file != null) {
            project.saveFile(replaceItems, file.getPath());
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

    private void addContextMenuOnList() {

//        replacesList.setCellFactory(lv -> {
//
//            ListCell<PEReplaceItem> cell = new ListCell<>();
//
//            ContextMenu contextMenu = new ContextMenu();
//
//            MenuItem editItem = new MenuItem("Edit");
//            editItem.setOnAction(event -> {
//                if(cell.getItem() != null) doSelectReplaceItem(cell.getItem());
//            });
//            MenuItem deleteItem = new MenuItem("Delete");
//            deleteItem.setOnAction(event -> {
//                cell.getItem().stringItem.setTranslated(false);
//                replacesList.getItems().remove(cell.getItem());
//            });
//            contextMenu.getItems().addAll(editItem, deleteItem);
//
//            cell.setContextMenu(contextMenu);
//
//            cell.textProperty().bind(Bindings.createStringBinding(
//                () -> Objects.toString(cell.getItem(), ""),
//                cell.itemProperty()
//            ));
//
//            return cell;
//        });

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

    private void createTableColumns() {

        TableColumn<PEReplaceItem, String> column1 = new TableColumn<>("Offset");
        column1.setCellValueFactory(new PropertyValueFactory<>("offset"));

        TableColumn<PEReplaceItem, String> column2 = new TableColumn<>("Original text");
        column2.setCellValueFactory(new PropertyValueFactory<>("origValue"));
        column2.setPrefWidth(200);

        TableColumn<PEReplaceItem, String> column3 = new TableColumn<>("New text");
        column3.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        column3.setPrefWidth(200);

        tableView.getColumns().addAll(column1, column2, column3);
        tableView.setItems(replaceItems);

        tableView.setRowFactory( tv -> {

            TableRow<PEReplaceItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    PEReplaceItem rowData = row.getItem();
                    if(rowData != null) doSelectReplaceItem(rowData);
                }
            });

            ContextMenu contextMenu = new ContextMenu();
            MenuItem jumpItem = new MenuItem("Show neighboring strings");
            MenuItem deleteItem = new MenuItem("Delete");
            jumpItem.setOnAction(event -> {

                int ofs = row.getItem().stringItem.offset;
                PEStringItem psi = stringsList.getItems().stream().filter(it -> it.offset == ofs).findFirst().orElse(null);
                int index = stringsList.getItems().indexOf(psi);
                stringsList.getSelectionModel().select(index);
                stringsList.scrollTo(index);

            });
            deleteItem.setOnAction(event -> {

                row.getItem().stringItem.setTranslated(false);
                replaceItems.remove(row.getItem());

            });
            contextMenu.getItems().addAll(jumpItem, deleteItem);
            row.setContextMenu(contextMenu);

            return row ;
        });

    }

    public void onTranslateClick(ActionEvent actionEvent) {

        String t = originalTextView.getText();
        if(t.equals("")) return;

        if(AppSettings.aimToken.equals("") && AppSettings.folderId.equals("")) {
            String url = "https://translate.google.com/?sl=" + AppSettings.translateSource + "&tl=" + AppSettings.translateTarget + "&op=translate&text=" + t;
            hostServices.showDocument(NetUtils.encodeURL(url));
            return;
        }

        //=================== yandex.translate =======================

        String resultText = null;
        try {
            resultText = Translate.translateYandex(t);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "The service returns an error. Check the AIM token", ButtonType.CLOSE).showAndWait();
            return;
        }

        if(resultText == null) {
            new Alert(Alert.AlertType.ERROR, "The service returns an error. Check the AIM token", ButtonType.CLOSE).showAndWait();
            return;
        }

        newTextView.setText(resultText);
        onTextsViewTyped(null);

    }

    public void onOpenSettings(ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        FXMLLoader fxml = new FXMLLoader(Application.class.getResource("options-view.fxml"));
        Scene scene = new Scene(fxml.load());
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.stage);
        stage.setResizable(false);
        OptionsController ctrl = fxml.getController();
        ctrl.onLoad(hostServices);
        stage.show();

    }

    public void onClearTranslatedSearch(ActionEvent actionEvent) {
        translatedSearchView.clear();
        onTranslatedTextTyped(null);
        translatedSearchView.requestFocus();
    }

    public void onClearSearchBox(ActionEvent actionEvent) {
        searchBox.clear();
        onUserStrictFilter(null);
        searchBox.requestFocus();
    }

    public void onTranslatedTextTyped(KeyEvent keyEvent) {

        String sample = translatedSearchView.getText().toLowerCase();

        if(sample.equals("")) {
            tableView.setItems(replaceItems);
        } else {
            List<PEReplaceItem> items = replaceItems.stream()
                    .filter(rItem -> rItem.newText.toLowerCase().contains(sample) || rItem.stringItem.data.toLowerCase().contains(sample))
                    .collect(Collectors.toList());
            tableView.setItems(FXCollections.observableArrayList(items));
        }

    }
}