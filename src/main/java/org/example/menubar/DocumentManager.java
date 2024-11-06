package org.example.menubar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.menubar.DatabaseManager.SQL_connect;

public class DocumentManager {

    private DatabaseManager dbManager;

    public DocumentManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void insertDocument(String id, String title, String author, String publisher, String publishedDate) {
        String sql = "INSERT INTO documents(id, title, author, publisher, publishedDate) VALUES(?,?,?,?,?)";

        try (Connection conn = SQL_connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, publisher);
            pstmt.setString(5, publishedDate);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Tài liệu đã được thêm thành công.");
            } else {
                System.out.println("Không thể thêm tài liệu.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

    public boolean borrowDocument(String id) {
        return false;
    }

}
