package org.example.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserLogin class is responsible for validating the user login credentials.
 * It queries the database to check if the username and password match any records.
 */
public class UserLogin {

    /**
     * Validates the login credentials by checking the username and password in the database.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return a LoginResult object containing the user ID and admin status if login is successful, null otherwise
     */
    public LoginResult validateLogin(String username, String password) {
        String sql = "SELECT id, is_admin FROM users WHERE user_name = ? AND password = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new LoginResult(rs.getString("id"), rs.getInt("is_admin") == 1);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage());
        }
        return null;
    }

    /**
     * LoginResult class is used to return the result of a login attempt,
     * including the user ID and whether the user is an admin.
     */
    public static class LoginResult {
        public final String userId;
        public final boolean isAdmin;

        /**
         * Constructs a LoginResult with the given user ID and admin status.
         *
         * @param userId the ID of the user
         * @param isAdmin whether the user has admin privileges
         */
        public LoginResult(String userId, boolean isAdmin) {
            this.userId = userId;
            this.isAdmin = isAdmin;
        }
    }
}
