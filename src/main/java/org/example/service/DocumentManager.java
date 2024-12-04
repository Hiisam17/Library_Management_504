package org.example.service;

import org.example.repository.DatabaseManager;
import org.example.model.Document;
import org.example.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.repository.DatabaseManager.getInstance;
import static org.example.util.DialogUtils.showAlert;
/**
 * Handles operations related to document management, including CRUD functionalities.
 */
public class DocumentManager {

    DatabaseManager dbManager = DatabaseManager.getInstance();

    /**
     * Constructs a new DocumentManager with a specified DatabaseManager.
     *
     * @param dbManager the database manager instance
     */
    public DocumentManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Checks if a document with the same title, author, and published date already exists in the database.
     *
     * @param title         the title of the document
     * @param author        the author of the document
     * @param publishedDate the published date of the document
     * @return true if a duplicate document exists, false otherwise
     */
    private boolean isDocumentDuplicate(String title, String author, String publishedDate) {
        String checkDuplicateSql =
                "SELECT COUNT(*) FROM document WHERE title = ? AND author = ? AND publishedDate = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkDuplicateSql)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, publishedDate);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Assume duplicate in case of error
        }
    }

    /**
     * Inserts a new document into the database.
     *
     * @param id            the ID of the document
     * @param title         the title of the document
     * @param author        the author of the document
     * @param publisher     the publisher of the document
     * @param publishedDate the published date of the document
     * @return true if the document was successfully inserted, false otherwise
     */
    public boolean insertDocument(
            String id, String title, String author, String publisher, String publishedDate) {

        String checkIdSql = "SELECT COUNT(*) FROM document WHERE id = ?";
        String insertSql =
                "INSERT INTO document (id, title, author, publisher, publishedDate) VALUES (?, ?, ?, ?, ?)";
        String url = "jdbc:sqlite:data/liba.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection is closed!");
                return false;
            }

            // Check if the ID already exists
            try (PreparedStatement checkIdStmt = conn.prepareStatement(checkIdSql)) {
                checkIdStmt.setString(1, id);
                ResultSet rsId = checkIdStmt.executeQuery();

                if (rsId.next() && rsId.getInt(1) > 0) {
                    showAlert("Error", "ID đã tồn tại!");
                    return false;
                }
            }

            // Check for duplicate documents
            if (isDocumentDuplicate(title, author, publishedDate)) {
                showAlert("Error", "Tài liệu đã tồn tại!");
                return false;
            }

            // Insert the new document
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
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a document from the database by its ID.
     *
     * @param id the ID of the document to delete
     * @return true if the document was successfully deleted, false otherwise
     */
    public boolean deleteDocumentById(String id) {
        String checkSql = "SELECT COUNT(*) FROM document WHERE id = ?";
        String deleteSql = "DELETE FROM document WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            // Check if the document exists
            checkStmt.setString(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return false; // Document does not exist
            }

            // Delete the document
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, id);
                deleteStmt.executeUpdate();
            }

            return true; // Successfully deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error while deleting
        }
    }

    /**
     * Updates the details of an existing document.
     *
     * @param document the document to be updated with new details
     */
    public void updateDocument(Document document) {
        // Kiểm tra xem tài liệu có trùng lặp hay không trước khi cập nhật
        if (isDocumentDuplicate(document.getTitle(), document.getAuthor(), document.getPublishedDate())) {
            showAlert("Lỗi", "Tài liệu đã tồn tại, không thể cập nhật.");
            return; // Dừng lại nếu tài liệu trùng lặp
        }

        String sql = "UPDATE document SET title = ?, author = ?, publisher = ?, publishedDate = ? WHERE id = ?";

        try (Connection conn = getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getAuthor());
            pstmt.setString(3, document.getPublisher());
            pstmt.setString(4, document.getPublishedDate());
            pstmt.setString(5, document.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Thành công", "Cập nhật tài liệu thành công.");
            } else {
                showAlert("Lỗi", "Không tìm thấy tài liệu với ID: " + document.getId());
            }

        } catch (SQLException e) {
            showAlert("Error", "Lỗi khi cập nhật tài liệu: " + e.getMessage());
        }
    }
    /**
     * Borrows a document for a specific user.
     *
     * @param documentId the ID of the document to be borrowed
     * @param userId     the ID of the user borrowing the document
     * @return true if the document was successfully borrowed, false otherwise
     */
    public boolean borrowDocument(String documentId, String userId) {
        System.out.println("User ID: " + userId); // Kiểm tra giá trị userId
        String checkAvailabilitySql = "SELECT isAvailable FROM document WHERE id = ?";
        String borrowSql = "UPDATE document SET isAvailable = 0 WHERE id = ?"; // Đặt là 0 để đánh dấu là đã mượn
        String insertBorrowedSql = "INSERT INTO borrowed_documents (user_id, document_id, borrow_date) VALUES (?, ?, ?)";

        try (Connection conn = getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkAvailabilitySql);
             PreparedStatement borrowStmt = conn.prepareStatement(borrowSql);
             PreparedStatement insertBorrowedStmt = conn.prepareStatement(insertBorrowedSql)) {

            // Kiểm tra xem tài liệu có sẵn để mượn hay không
            checkStmt.setString(1, documentId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int isAvailable = rs.getInt("isAvailable"); // Lấy giá trị dưới dạng số nguyên
                if (isAvailable != 1) { // Kiểm tra nếu không phải là 1 thì tài liệu không sẵn sàng để mượn
                    System.out.println("Tài liệu đã được mượn.");
                    return false;
                }
            } else {
                System.out.println("Không tìm thấy tài liệu với ID: " + documentId);
                return false;
            }

            // Cập nhật trạng thái của tài liệu để đánh dấu là đã mượn
            borrowStmt.setString(1, documentId);
            int rowsAffected = borrowStmt.executeUpdate();
            if (rowsAffected > 0) {
                // Chèn thông tin vào bảng borrowed_documents
                insertBorrowedStmt.setString(1, userId);
                insertBorrowedStmt.setString(2, documentId);
                insertBorrowedStmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                insertBorrowedStmt.executeUpdate();

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

    /**
     * Retrieves all documents from the database.
     *
     * @return a list of all documents available in the database
     */
    public List<Document> getAllDocument() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT id, title, author, publisher, publishedDate, isAvailable FROM document";

        try (Connection conn = getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Document document = new Document();
                document.setId(rs.getString("id"));
                document.setTitle(rs.getString("title"));
                document.setAuthor(rs.getString("author"));
                document.setPublisher(rs.getString("publisher"));
                document.setPublishedDate(rs.getString("publishedDate"));
                document.setIsAvailable(rs.getBoolean("isAvailable")); // Chuyển đổi giá trị từ số nguyên sang boolean
                documents.add(document);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy dữ liệu tài liệu: " + e.getMessage());
        }

        return documents;
    }

    /**
     * Searches for documents in the database that match the given keyword in the title or author.
     *
     * @param keyword the search keyword, which will be matched against the title and author.
     * @return a list of {@link Document} objects that match the search criteria.
     */
    public List<Document> searchDocuments(String keyword) {
        List<Document> results = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE title LIKE ? OR author LIKE ?";

        try (Connection conn = getInstance().getConnection();
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
                        rs.getString("publishedDate"),
                        rs.getBoolean("isAvailable")
                );
                results.add(document);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm tài liệu: " + e.getMessage());
        }
        return results;
    }

    /**
     * Retrieves all documents borrowed by a specific user based on their user ID.
     *
     * @param userId the ID of the user whose borrowed documents are to be retrieved.
     * @return a list of {@link Document} objects representing the borrowed documents.
     */
    public List<Document> getBorrowedDocumentsByUserId(String userId) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT d.* FROM document d " +
                "JOIN borrowed_documents bd ON d.id = bd.document_id " +
                "JOIN users u ON bd.user_id = u.id " +
                "WHERE u.id >= 4 AND u.id = ?";
        String url = "jdbc:sqlite:data/liba.db";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Document document = new Document();
                document.setId(rs.getString("id"));
                document.setTitle(rs.getString("title"));
                document.setAuthor(rs.getString("author"));
                document.setPublisher(rs.getString("publisher"));
                document.setPublishedDate(rs.getString("publishedDate"));
                document.setIsAvailable(rs.getInt("isAvailable") == 0);
                documents.add(document);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi lấy dữ liệu tài liệu: " + e.getMessage());
        }

        return documents;
    }

    /**
     * Returns a borrowed document back to the library system.
     *
     * @param documentId the ID of the document to be returned.
     * @param userId the ID of the user returning the document.
     * @return {@code true} if the document was successfully returned, {@code false} otherwise.
     */
    public boolean returnDocument(String documentId, String userId) {
        String checkBorrowedSql = "SELECT * FROM borrowed_documents WHERE document_id = ? AND user_id = ?";
        String returnSql = "UPDATE document SET isAvailable = 1 WHERE id = ?";
        String deleteBorrowedSql = "DELETE FROM borrowed_documents WHERE document_id = ? AND user_id = ?";
        String url = "jdbc:sqlite:data/liba.db";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement checkStmt = conn.prepareStatement(checkBorrowedSql);
             PreparedStatement returnStmt = conn.prepareStatement(returnSql);
             PreparedStatement deleteBorrowedStmt = conn.prepareStatement(deleteBorrowedSql)) {

            // Kiểm tra xem tài liệu có được mượn bởi người dùng này không
            checkStmt.setString(1, documentId);
            checkStmt.setString(2, userId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Tài liệu không được mượn bởi người dùng này.");
                return false;
            }

            // Cập nhật trạng thái của tài liệu để đánh dấu là đã trả
            returnStmt.setString(1, documentId);
            int rowsAffected = returnStmt.executeUpdate();
            if (rowsAffected > 0) {
                // Xóa thông tin mượn tài liệu khỏi bảng borrowed_documents
                deleteBorrowedStmt.setString(1, documentId);
                deleteBorrowedStmt.setString(2, userId);
                deleteBorrowedStmt.executeUpdate();

                System.out.println("Trả tài liệu thành công.");
                return true;
            } else {
                System.out.println("Không thể trả tài liệu.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi trả tài liệu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a review for a document.
     *
     * @param documentId the ID of the document being reviewed.
     * @param userId the ID of the user submitting the review.
     * @param rating the rating given by the user, typically a value between 1 and 5.
     * @param comment a comment or feedback provided by the user about the document.
     * @return {@code true} if the review was successfully added, {@code false} otherwise.
     */
    public boolean addReview(String documentId, String userId, int rating, String comment) {
        String sql = "INSERT INTO reviews(documentId, userId, rating, comment) VALUES(?, ?, ?, ?)";

        try (Connection conn = getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, documentId);
            pstmt.setString(2, userId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comment);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all reviews for a specific document, including user information.
     *
     * @param documentId the ID of the document for which reviews are retrieved.
     * @return a list of {@link Review} objects containing review details and associated user information.
     */
    public List<Review> getReviews(String documentId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS userName FROM reviews r JOIN users u ON r.userId = u.id WHERE r.documentId = ?";

        try (Connection conn = getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, documentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review();
                review.setId(rs.getInt("id"));
                review.setDocumentId(rs.getString("documentId"));
                review.setUserId(rs.getString("userId"));
                review.setUserName(rs.getString("userName")); // Set user name
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));

                // Kiểm tra giá trị null trước khi gọi toLocalDateTime()
                Timestamp timestamp = rs.getTimestamp("timestamp");
                if (timestamp != null) {
                    review.setTimestamp(timestamp.toLocalDateTime());
                } else {
                    review.setTimestamp(null); // Hoặc giá trị mặc định khác nếu cần
                }

                reviews.add(review);

                // In ra để debug
                System.out.println("Review ID: " + review.getId());
                System.out.println("User Name: " + review.getUserName());
                System.out.println("Rating: " + review.getRating());
                System.out.println("Comment: " + review.getComment());
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return reviews;
    }

    /**
     * Retrieves the total number of books in the database.
     *
     * @return the total count of books in the database.
     * @throws SQLException if a database access error occurs.
     */
    public int getTotalBooksFromDatabase() throws SQLException {
        String query = "SELECT COUNT(*) FROM document";
        try (Connection conn = getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Retrieves the total number of books currently available for borrowing.
     *
     * @return the total count of available books in the database.
     * @throws SQLException if a database access error occurs.
     */
    public int getAvailableBooksFromDatabase() throws SQLException {
        String query = "SELECT COUNT(*) FROM document WHERE isAvailable = 1";
        try (Connection conn = getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
