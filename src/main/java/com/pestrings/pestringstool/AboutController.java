package com.pestrings.pestringstool;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URI;

public class AboutController {

    public void onClickGithub(ActionEvent actionEvent) {

        try {
            new ProcessBuilder("x-www-browser", "https://github.com/redspirit/PEStringsTool").start();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Desktop.getDesktop().browse(new URI("https://github.com/redspirit/PEStringsTool"));
//        getHostServices().showDocument("https://github.com/redspirit/PEStringsTool");

    }

}