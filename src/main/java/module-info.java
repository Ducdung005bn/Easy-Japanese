module org.example.easyjapanese {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires freetts;

    opens org.example.easyjapanese to javafx.fxml;
    exports org.example.easyjapanese;
}