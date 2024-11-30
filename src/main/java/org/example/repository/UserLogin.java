package org.example.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLogin {
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

    // Lớp LoginResult dùng để trả kết quả xác thực
    public static class LoginResult {
        public final String userId;
        public final boolean isAdmin;

        public LoginResult(String userId, boolean isAdmin) {
            this.userId = userId;
            this.isAdmin = isAdmin;
        }
    }
}
