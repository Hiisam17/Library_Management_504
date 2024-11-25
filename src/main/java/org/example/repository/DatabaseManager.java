package org.example.repository;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:liba.db";

    public DatabaseManager(String url) {
        //dropTableIfExists(); // Xóa bảng nếu tồn tại
    }

    public DatabaseManager() {
        this(DB_URL); // Gọi constructor khác với URL mặc định
    }

    // Phương thức khởi tạo kết nối
    public static Connection SQL_connect() {
        Connection conn = null;
        try {
            // Đường dẫn đến cơ sở dữ liệu SQLite
            String url = "jdbc:sqlite:data/liba.db"; // Thay đổi đường dẫn nếu cần
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
        return conn; // Nếu kết nối thất bại, conn sẽ là null
    }

    public static void createTables() {
        String createBorrowedDocumentsTable = "CREATE TABLE IF NOT EXISTS borrowed_documents (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT NOT NULL, " +
                "document_id TEXT NOT NULL, " +
                "borrow_date DATE NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (document_id) REFERENCES document(id)" +
                ");";

        try (Connection conn = SQL_connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createBorrowedDocumentsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

