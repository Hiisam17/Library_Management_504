module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires java.sql;
    requires java.desktop;

    //opens org.example.menubar to javafx.fxml;
    //opens org.example.usermenu to javafx.fxml;

    exports org.example.controller.document;
    opens org.example.controller.document to javafx.fxml;
    exports org.example.controller.auth;
    opens org.example.controller.auth to javafx.fxml;
    exports org.example.controller.menu;
    opens org.example.controller.menu to javafx.fxml;
    exports org.example.controller.review;
    opens org.example.controller.review to javafx.fxml;
    exports org.example.model;
    opens org.example.model to javafx.fxml;
    exports org.example.service;
    opens org.example.service to javafx.fxml;
    exports org.example.repository;
    opens org.example.repository to javafx.fxml;
    exports org.example.util;
    opens org.example.util to javafx.fxml;
    exports org.example;
    opens org.example to javafx.fxml;
}