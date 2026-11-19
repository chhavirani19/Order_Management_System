package com.example.ordermanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * (a) Establishes the connection with the MySQL database ecommerce_db.
 * Used by both the console program and the REST API.
 */
public class DBConnection {

    // ---- Change these to match your MySQL setup ----
    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
