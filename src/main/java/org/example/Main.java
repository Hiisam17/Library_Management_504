package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.auth.LoginController;
import org.example.repository.DatabaseManager;
import org.example.service.UserService;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {
    private Stage primaryStage;
    private UserService userService;
    private static Main instance;
    public Main() {
        // Constructor trống
    }
    @Override
    public void start(Stage primaryStage) throws IOException {

        DatabaseManager.createTables();

        if (this.primaryStage == null) {
            this.primaryStage = primaryStage;
            userService = new UserService();
            instance = this;
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/views/auth/hello-view.fxml")));
            Parent root = loader.load();

            // Lấy LoginController và gọi setMainApp
            LoginController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(root, 1200, 800);

            // Thêm đoạn code để tải file CSS
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/auth/login.css")).toExternalForm());

            primaryStage.setTitle("Đăng nhập");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Không cho phép thay đổi kích thước
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                Platform.exit(); // Kết thúc JavaFX Application Thread
                System.exit(0);  // Kết thúc tất cả tiến trình
            });
        }
    }
    public void restartApp() throws IOException {

        if (primaryStage != null) {
            // Đóng cửa sổ hiện tại và làm mới
            primaryStage.close();
            try {
                instance = this;
                // Tạo lại cửa sổ đăng nhập
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/views/auth/hello-view.fxml")));
                Parent root = loader.load();

                // Lấy controller và gọi setMainApp
                LoginController controller = loader.getController();
                controller.setMainApp(this);

                Scene scene = new Scene(root, 1200, 800);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/auth/login.css")).toExternalForm());

                // Tạo một cửa sổ mới
                primaryStage.setTitle("Đăng nhập");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false); // Không cho phép thay đổi kích thước
                primaryStage.show();
                primaryStage.setOnCloseRequest(event -> {
                    Platform.exit(); // Kết thúc JavaFX Application Thread
                    System.exit(0);  // Kết thúc tất cả tiến trình
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static Main getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Main chưa được khởi tạo.");
        }
        return instance;
    }

    @Override
    public void stop() {
        // Đóng kết nối cơ sở dữ liệu khi ứng dụng thoát
        DatabaseManager.getInstance().closeConnection();
        System.out.println("Database connection closed.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

