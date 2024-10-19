package org.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
<<<<<<< HEAD:src/main/java/org/example/dao/UserDAO.java
public class UserDAO {
    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (user_name, password, is_admin) VALUES (?, ?, false)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int rowsAffected = pstmt.executeUpdate();// true nếu đăng ký thành công

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:data/liba.db";
            // Kết nối tới cơ sở dữ liệu
            conn = DriverManager.getConnection(url);
            System.out.println("Kết nối thành công đến cơ sở dữ liệu!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
=======

import java.io.IOException;
import java.util.Objects;
//public class UserDAO {
//    public boolean register(String username, String password) {
//        String sql = "INSERT INTO users (user_name, password, is_admin) VALUES (?, ?, false)";
//
//        try (Connection conn = connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, username);
//            pstmt.setString(2, password);
//
//            int rowsAffected = pstmt.executeUpdate();// true nếu đăng ký thành công
//
//            return rowsAffected > 0;
//
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//
//        return false;
//    }
//    public static Connection connect() {
//        Connection conn = null;
//        try {
//            String url = "jdbc:sqlite:data/liba.db";
//            // Kết nối tới cơ sở dữ liệu
//            conn = DriverManager.getConnection(url);
//            System.out.println("Kết nối thành công đến cơ sở dữ liệu!");
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return conn;
//    }
//}
>>>>>>> c57d04e55995a5af2cb6d70a741098f6450e3aab:src/main/java/UserDAO.java
// tính năng đăng ký dùng dòng lệnh