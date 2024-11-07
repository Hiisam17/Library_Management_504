package org.example.menubar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {
    private Stage primaryStage;
    private UserService userService;
    private static Main instance;

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (this.primaryStage == null) {
            this.primaryStage = primaryStage;
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
    }
    public void restartApp() {
        if (primaryStage != null) {
            // Đóng cửa sổ hiện tại và làm mới
            primaryStage.close();
            try {
                // Tạo lại cửa sổ đăng nhập
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
                Parent root = loader.load();

                // Lấy controller và gọi setMainApp
                LoginController controller = loader.getController();
                controller.setMainApp(this);

                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("login.css")).toExternalForm());

                // Tạo một cửa sổ mới
                Stage newStage = new Stage();
                newStage.setTitle("Đăng nhập");
                newStage.setScene(scene);
                newStage.show(); // Hiển thị cửa sổ mới

                // Cập nhật primaryStage để giữ tham chiếu đến cửa sổ mới
                primaryStage = newStage;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }
    public UserService getUserService() {
        return userService;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

