package org.example.easyjapanese;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VocabularyApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VocabularyView.fxml"));

        Scene scene = new Scene(loader.load(), 800, 600);
        primaryStage.setTitle("Main View");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
