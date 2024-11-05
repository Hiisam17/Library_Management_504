module org.example.menubar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires java.sql;
    requires java.desktop;

    opens org.example.menubar to javafx.fxml;
    opens org.example.usermenu to javafx.fxml;

    exports org.example.menubar;
    exports org.example.usermenu;
}