package org.example.menubar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLlib {
    public static Connection SQL_connect() {
        Connection connection = null;
        try {
            String url = "jdbc:sqlite:data/liba.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Kết nối thành công đến cơ sở dữ liệu!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

}
