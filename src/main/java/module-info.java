module org.example.easyjapanese {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires freetts;
    requires java.desktop;
    requires javafx.media;
    requires org.json;
    requires org.jsoup;

    opens org.example.easyjapanese to javafx.fxml;
    exports org.example.easyjapanese;
}