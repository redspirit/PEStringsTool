package com.pestrings.pestringstool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("PEStringsTool by RedSpirit");
        stage.setScene(scene);
        stage.show();

        PEReader reader = new PEReader();
        MainController mainController = fxmlLoader.getController();
        mainController.setStage(stage);
        mainController.setReader(reader);

//        reader.applyChanges(mainController.replacesList.getItems(), "");

        //reader.loadFile("/Users/spirit/Documents/Garden Story v1.0.3/EXE/hello/hello_str.exe");



    }

    public static void main(String[] args) {
        launch();
    }
}