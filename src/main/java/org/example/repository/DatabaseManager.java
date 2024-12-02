package org.example.repository;

import org.example.service.DatabaseService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * DatabaseManager class is a singleton responsible for managing the database connection and performing database operations.
 * It handles connection management, table creation, and other database interactions.
 */
public class DatabaseManager {

    private static DatabaseManager instance; // Singleton instance
    public static final String DB_URL = "jdbc:sqlite:data/liba.db"; // Đường dẫn cơ sở dữ liệu
    private Connection connection; // Kết nối cơ sở dữ liệu

    /**
     * Private constructor to prevent external instantiation of the DatabaseManager.
     * It initializes the connection to the database.
     */
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Kết nối cơ sở dữ liệu thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi kết nối cơ sở dữ liệu!");
        }
    }

    /**
     * Returns the singleton instance of DatabaseManager.
     * If the instance does not exist, it creates a new one.
     *
     * @return the singleton instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Provides access to the database connection.
     * If the connection is closed, it reopens the connection.
     *
     * @return the current database connection
     */
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

    /**
     * Creates necessary tables in the database, such as the 'borrowed_documents' table.
     * This method is executed asynchronously.
     */
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

    /**
     * Closes the current database connection.
     * It is important to call this method to release database resources.
     */
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
