module org.example.menubar {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.menubar to javafx.fxml;
    exports org.example.menubar;
}