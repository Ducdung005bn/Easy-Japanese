module org.example.easyjapanese {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires freetts;
    requires javafx.media;

    opens org.example.easyjapanese to javafx.fxml;
    exports org.example.easyjapanese;
}