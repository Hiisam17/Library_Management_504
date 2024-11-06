package org.example.menubar;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.menubar.DatabaseManager.SQL_connect;

public class DocumentManager {

    private DatabaseManager dbManager;

    public DocumentManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean insertDocument(String id, String title, String author, String publisher, String publishedDate) {
        String checkIdSql = "SELECT COUNT(*) FROM documents WHERE id = ?";
        String checkDuplicateSql = "SELECT COUNT(*) FROM documents WHERE title = ? AND author = ? AND publishedDate = ?";
        String insertSql = "INSERT INTO documents (id, title, author, publisher, publishedDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = SQL_connect();
             PreparedStatement checkIdStmt = conn.prepareStatement(checkIdSql);
             PreparedStatement checkDuplicateStmt = conn.prepareStatement(checkDuplicateSql)) {

            // Kiểm tra trùng ID
            checkIdStmt.setString(1, id);
            ResultSet rsId = checkIdStmt.executeQuery();
            if (rsId.next() && rsId.getInt(1) > 0) {
                showAlert("Error", "ID đã tồn tại!", Alert.AlertType.ERROR);
                return false;
            }

            // Kiểm tra trùng title, author, publishedDate
            checkDuplicateStmt.setString(1, title);
            checkDuplicateStmt.setString(2, author);
            checkDuplicateStmt.setString(3, publishedDate);
            ResultSet rsDuplicate = checkDuplicateStmt.executeQuery();
            if (rsDuplicate.next() && rsDuplicate.getInt(1) > 0) {
                showAlert("Error", "Tài liệu đã tồn tại!", Alert.AlertType.ERROR);
                return false;
            }

            // Nếu không có trùng lặp, thêm tài liệu mới
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, id);
                insertStmt.setString(2, title);
                insertStmt.setString(3, author);
                insertStmt.setString(4, publisher);
                insertStmt.setString(5, publishedDate);
                int rowsAffected = insertStmt.executeUpdate();

                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }



    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM documents";

        try (Connection conn = SQL_connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Document document = new Document();
                document.setId(rs.getString("id"));
                document.setTitle(rs.getString("title"));
                document.setAuthor(rs.getString("author"));
                document.setPublisher(rs.getString("publisher"));
                document.setPublishedDate(rs.getString("publishedDate"));
                documents.add(document);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return documents;
    }

    public void deleteDocumentById(String id) {
        String sql = "DELETE FROM documents WHERE id = ?";

        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Đã xóa tài liệu có ID: " + id);
            } else {
                System.out.println("Không tìm thấy tài liệu với ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa tài liệu: " + e.getMessage());
        }
    }

    public void updateDocument(Document document) {
        String sql = "UPDATE documents SET title = ?, author = ?, publisher = ?, publishedDate = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getAuthor());
            pstmt.setString(3, document.getPublisher());
            pstmt.setString(4, document.getPublishedDate());
            pstmt.setString(5, document.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cập nhật tài liệu thành công.");
            } else {
                System.out.println("Không tìm thấy tài liệu với ID: " + document.getId());
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật tài liệu: " + e.getMessage());
        }
    }

    public boolean borrowDocument(String id) {
        String checkAvailabilitySql = "SELECT isAvailable FROM document WHERE id = ?";
        String borrowSql = "UPDATE document SET isAvailable = 0 WHERE id = ?"; // Đặt là 0 để đánh dấu là đã mượn

        try (Connection conn = SQL_connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkAvailabilitySql);
             PreparedStatement borrowStmt = conn.prepareStatement(borrowSql)) {

            // Kiểm tra xem tài liệu có sẵn để mượn hay không
            checkStmt.setString(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int isAvailable = rs.getInt("isAvailable"); // Lấy giá trị dưới dạng số nguyên
                System.out.println("isAvailable value from DB: " + isAvailable); // In giá trị để kiểm tra

                if (isAvailable != 1) { // Kiểm tra nếu không phải là 1 thì tài liệu không sẵn sàng để mượn
                    System.out.println("Tài liệu đã được mượn.");
                    return false;
                }
            } else {
                System.out.println("Không tìm thấy tài liệu với ID: " + id);
                return false;
            }

            // Cập nhật trạng thái của tài liệu để đánh dấu là đã mượn
            borrowStmt.setString(1, id);
            int rowsAffected = borrowStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mượn tài liệu thành công.");
                return true;
            } else {
                System.out.println("Không thể mượn tài liệu.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi mượn tài liệu: " + e.getMessage());
            return false;
        }
    }


    public List<Document> getAllDocument() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT id, title, author, publisher, publishedDate FROM document";

        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Document document = new Document();
                document.setId(rs.getString("id"));
                document.setTitle(rs.getString("title"));
                document.setAuthor(rs.getString("author"));
                document.setPublisher(rs.getString("publisher"));
                document.setPublishedDate(rs.getString("publishedDate"));
                documents.add(document);
            }

            System.out.println("Successfully retrieved " + documents.size() + " documents.");

        } catch (SQLException e) {
            System.err.println("Error retrieving documents: " + e.getMessage());
        }

        return documents;
    }
    public List<Document> searchDocuments(String keyword) {
        List<Document> results = new ArrayList<>();
        String sql = "SELECT * FROM documents WHERE title LIKE ? OR author LIKE ?";

        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Document document = new Document(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("publishedDate")
                );
                results.add(document);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm tài liệu: " + e.getMessage());
        }
        return results;
    }

}
