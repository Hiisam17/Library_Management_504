module org.example.menubar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires java.desktop;
    requires java.sql;



    opens org.example.menubar to javafx.fxml;
    exports org.example.menubar;
}