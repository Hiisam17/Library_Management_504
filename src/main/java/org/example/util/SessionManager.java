package org.example.util;

import javafx.stage.Stage;
import org.example.Main;

import java.io.IOException;

/**
 * SessionManager is a singleton class responsible for managing user sessions,
 * including performing logout actions and restarting the application.
 */
public class SessionManager {
    private static SessionManager instance;

    /**
     * Private constructor to prevent instantiation.
     */
    public SessionManager() {}

    /**
     * Returns the singleton instance of the SessionManager.
     *
     * @return the singleton instance of SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Performs the logout operation by closing the current stage and restarting the application.
     *
     * @param stage the current stage to close before logging out
     */
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
