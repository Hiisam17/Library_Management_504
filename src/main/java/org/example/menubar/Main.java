package org.example.menubar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {
    private UserService userService;

    @Override
    public void start(Stage primaryStage) throws IOException {

        userService = new UserService();

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        Parent root = loader.load();

        // Lấy LoginController và gọi setMainApp
        LoginController controller = loader.getController();
        controller.setMainApp(this);

        Scene scene = new Scene(root, 600, 400);

        // Thêm đoạn code để tải file CSS
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("login.css")).toExternalForm());

        primaryStage.setTitle("Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public UserService getUserService() {
        return userService;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

