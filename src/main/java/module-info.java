module org.example.easyjapanese {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires freetts;
    requires java.desktop;
    requires javafx.media;
    requires org.json;
    requires org.jsoup;
    requires org.apache.commons.lang3;

    opens org.example.easyjapanese to javafx.fxml;
    exports org.example.easyjapanese;
    exports utils;
    opens utils to javafx.fxml;
    exports flashcard;
    opens flashcard to javafx.fxml;
    exports quiz.monster;
    opens quiz.monster to javafx.fxml;
    exports quiz.award;
    opens quiz.award to javafx.fxml;
    exports quiz.controller;
    opens quiz.controller to javafx.fxml;
    exports quiz.function.controller;
    opens quiz.function.controller to javafx.fxml;
}