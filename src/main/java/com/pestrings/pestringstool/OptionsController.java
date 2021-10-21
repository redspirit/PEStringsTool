package com.pestrings.pestringstool;

import com.pestrings.pestringstool.utils.AppSettings;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class OptionsController {

    public ChoiceBox<String> sourceListView;
    public ChoiceBox<String> targetListView;

    public void onLoad() {

        sourceListView.getItems().addAll("auto", "en","ru","de","es","it","zh-CN","ko","ja");
        targetListView.getItems().addAll("en","ru","de","es","it","zh-CN","ko","ja");

        sourceListView.getSelectionModel().select(AppSettings.translateSource);
        targetListView.getSelectionModel().select(AppSettings.translateTarget);

    }

    public void onSave(ActionEvent actionEvent) {

        AppSettings.translateSource = sourceListView.getSelectionModel().getSelectedItem();
        AppSettings.translateTarget = targetListView.getSelectionModel().getSelectedItem();

        Stage stage = (Stage) sourceListView.getScene().getWindow();
        stage.close();
    }
}
