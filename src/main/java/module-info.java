module org.example.menubar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
<<<<<<< HEAD
    requires java.desktop;
=======
    requires java.sql;
>>>>>>> 7ffdbecdcc782fd8ef93f3d90910a75a82cc5c69


    opens org.example.menubar to javafx.fxml;
    exports org.example.menubar;
}