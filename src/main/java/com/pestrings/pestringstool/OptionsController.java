package com.pestrings.pestringstool;

import com.pestrings.pestringstool.utils.AppSettings;
import com.pestrings.pestringstool.utils.NetUtils;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OptionsController {

    public ChoiceBox<String> sourceListView;
    public ChoiceBox<String> targetListView;
    public TextField aimTokenView;
    public TextField folderIdView;
    public HostServices hostServices;

    public void onLoad(HostServices hs) {

        hostServices = hs;

        sourceListView.getItems().addAll("auto", "en","ru","de","es","it","zh-CN","ko","ja");
        targetListView.getItems().addAll("en","ru","de","es","it","zh-CN","ko","ja");

        sourceListView.getSelectionModel().select(AppSettings.translateSource);
        targetListView.getSelectionModel().select(AppSettings.translateTarget);

        aimTokenView.setText(AppSettings.aimToken);
        folderIdView.setText(AppSettings.folderId);

    }

    public void onSave(ActionEvent actionEvent) {

        AppSettings.translateSource = sourceListView.getSelectionModel().getSelectedItem();
        AppSettings.translateTarget = targetListView.getSelectionModel().getSelectedItem();

        AppSettings.aimToken = aimTokenView.getText();
        AppSettings.folderId = folderIdView.getText();

        AppSettings.save();

        Stage stage = (Stage) sourceListView.getScene().getWindow();
        stage.close();
    }

    public void onDocumentation(ActionEvent actionEvent) {

        hostServices.showDocument("https://cloud.yandex.ru/docs/translate/quickstart");

    }
}
