package com.pestrings.pestringstool;

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
        Scene scene = new Scene(fxmlMain.load(), 800, 600);
        stage.setTitle("PEStringsTool v0.1 by RedSpirit");
        stage.setScene(scene);
        stage.show();

        MainController mainController = fxmlMain.getController();
        mainController.setStage(stage);


        new ProjectManager().saveJson();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("CLOSE");
                //this line cancel the close request
//                event.consume();
            }
        });

    }

    // todo
    // 4. сделать оконо about с инфой об авторе и ссылке на репозиторий
    // 5. в строке статуса при сохранении перевода отображать количество добавленных переводов
    // 6. навесить контекстное меню на список переводов с пунктом удаления строки и редактирования строки
    // 7. при загрузке exe файла в статусе выводить: "game.exe is loaded, found 3432 strings"
    // 8. при загрузке нового файла надо очищать все поля



    public static void main(String[] args) {
        launch();
    }
}