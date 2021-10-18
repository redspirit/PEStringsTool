package com.pestrings.pestringstool;

import com.pestrings.pestringstool.gms.GMSDATA;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlMain = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlMain.load(), 1000, 700);
        stage.setTitle("PEStringsTool v0.1");
        stage.setScene(scene);
        stage.show();

        MainController mainController = fxmlMain.getController();
        mainController.setStage(stage);
        mainController.addContextMenuOnList();
        mainController.setHostServices(getHostServices());


        GMSDATA.loadFile("/home/spirit/hard/TEST/data.win");


        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("CLOSE");
                //this line cancel the close request
//                event.consume();
            }
        });

    }

    public static void main(String[] args) {
        launch();
    }
}