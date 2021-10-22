package com.pestrings.pestringstool;

import com.pestrings.pestringstool.utils.AppSettings;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutController {

    public Button closeButton;
    public HostServices hostServices;
    public Label nameVersionLabel;

    public void onClickGithub(ActionEvent actionEvent) {
        hostServices.showDocument("https://github.com/redspirit/PEStringsTool");
    }

    public void onCloseWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void setHostServices(HostServices hs) {
        hostServices = hs;
        nameVersionLabel.setText("PEStringsTool " + AppSettings.version);
    }
}