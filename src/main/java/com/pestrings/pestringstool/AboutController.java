package com.pestrings.pestringstool;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;

public class AboutController {

    public Button closeButton;

    public void onClickGithub(ActionEvent actionEvent) {

        try {
            new ProcessBuilder("x-www-browser", "https://github.com/redspirit/PEStringsTool").start();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Desktop.getDesktop().browse(new URI("https://github.com/redspirit/PEStringsTool"));
//        getHostServices().showDocument("https://github.com/redspirit/PEStringsTool");

    }

    public void onCloseWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}