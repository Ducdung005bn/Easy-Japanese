package org.example.easyjapanese;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VocabularyApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/easyjapanese/VocabularyAppView.fxml"));

        Scene scene = new Scene(loader.load(), 1275, 725);
        primaryStage.setTitle("Easy Japanese Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
