package org.example.menubar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentManager {

    private DatabaseManager dbManager;

    public DocumentManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void insertDocument(String id, String title, String author, String publisher, String publishedDate) {
        String sql = "INSERT INTO documents(id, title, author, publisher, publishedDate) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseManager.SQL_connect();
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

        try (Connection conn = DatabaseManager.SQL_connect();
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
}
