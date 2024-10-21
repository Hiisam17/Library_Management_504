package org.example.menubar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    // Phương thức khởi tạo kết nối
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Kết nối SQLite thành công.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}

