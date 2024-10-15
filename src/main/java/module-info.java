module org.example.menubar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;


    opens org.example.menubar to javafx.fxml;
    exports org.example.menubar;
}