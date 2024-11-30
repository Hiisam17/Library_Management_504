/*package org.example.repository;

import org.example.service.DatabaseService;

import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

        Future<String> result = DatabaseService.executeQueryAsync(createBorrowedDocumentsTable);
        try {
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}*/

package org.example.repository;

import org.example.service.DatabaseService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DatabaseManager {

    private static DatabaseManager instance; // Singleton instance
    public static final String DB_URL = "jdbc:sqlite:data/liba.db"; // Đường dẫn cơ sở dữ liệu
    private Connection connection; // Kết nối cơ sở dữ liệu

    // Private constructor để ngăn tạo object từ bên ngoài
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Kết nối cơ sở dữ liệu thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi kết nối cơ sở dữ liệu!");
        }
    }

    // Phương thức truy cập Singleton instance
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Phương thức truy cập Connection
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL); // Mở lại nếu bị đóng
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to reconnect to the database!");
        }
        return connection;
    }

    // Tạo bảng
    public static void createTables() {
        String createBorrowedDocumentsTable = "CREATE TABLE IF NOT EXISTS borrowed_documents (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT NOT NULL, " +
                "document_id TEXT NOT NULL, " +
                "borrow_date DATE NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (document_id) REFERENCES document(id)" +
                ");";

        Future<String> result = DatabaseService.executeQueryAsync(createBorrowedDocumentsTable);
        try {
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Đóng kết nối
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Đã đóng kết nối cơ sở dữ liệu.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


