package org.example.util;

import javafx.stage.Stage;
import org.example.Main;

import java.io.IOException;

public class SessionManager {
    private static SessionManager instance;

    public SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void performLogout(Stage stage) {
        // Đóng cửa sổ hiện tại
        if (stage != null) {
            stage.close();
        }

        // Gọi Main để khởi động lại ứng dụng
        try {
            Main mainInstance = Main.getInstance();
            if (mainInstance != null) {
                mainInstance.restartApp();
            } else {
                System.err.println("Lỗi: Main chưa được khởi tạo.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

