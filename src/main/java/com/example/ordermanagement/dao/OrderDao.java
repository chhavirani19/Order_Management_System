package com.example.ordermanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.ordermanagement.model.Order;

/**
 * DAO (Data Access Object) - every SQL query of the project is here.
 * Uses plain JDBC with PreparedStatement.
 */
@Repository
public class OrderDao {

    // Creates the Orders table if it does not exist yet
    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Orders ("
                + "order_id INT PRIMARY KEY, "
                + "customer_name VARCHAR(100), "
                + "product_name VARCHAR(100), "
                + "quantity INT, "
                + "unit_price DOUBLE, "
                + "status VARCHAR(30))";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    // (a) Insert one order. A duplicate order_id throws
    //     SQLIntegrityConstraintViolationException (primary key).
    public void insert(Order o) throws SQLException {
        String sql = "INSERT INTO Orders (order_id, customer_name, product_name, quantity, unit_price, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getOrderId());
            ps.setString(2, o.getCustomerName());
            ps.setString(3, o.getProductName());
            ps.setInt(4, o.getQuantity());
            ps.setDouble(5, o.getUnitPrice());
            ps.setString(6, o.getStatus());
            ps.executeUpdate();
        }
    }

    // (a) Duplicate check
    public boolean exists(int orderId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Orders WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // (b) Orders with the given status and quantity > 2, unit price high -> low
    public List<Order> findByStatusWithQuantityAbove2(String status) throws SQLException {
        String sql = "SELECT * FROM Orders WHERE status = ? AND quantity > 2 ORDER BY unit_price DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return toList(rs);
            }
        }
    }

    // (c) Increase quantity. Returns rows affected (0 = order does not exist)
    public int addQuantity(int orderId, int extraQuantity) throws SQLException {
        String sql = "UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, extraQuantity);
            ps.setInt(2, orderId);
            return ps.executeUpdate();
        }
    }

    // (d) Retrieve one order. Returns null if it does not exist
    public Order findById(int orderId) throws SQLException {
        String sql = "SELECT * FROM Orders WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? toOrder(rs) : null;
            }
        }
    }

    // (d) Update status. Returns rows affected
    public int updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate();
        }
    }

    // (e) Total order value using SUM(quantity * unit_price)
    public double getTotalOrderValue() throws SQLException {
        String sql = "SELECT SUM(quantity * unit_price) AS total_value FROM Orders";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble("total_value") : 0;   // SUM of empty table = NULL -> 0
        }
    }

    // (e) Delete only if status is Cancelled. Returns rows affected
    public int deleteIfCancelled(int orderId) throws SQLException {
        String sql = "DELETE FROM Orders WHERE order_id = ? AND status = 'Cancelled'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate();
        }
    }

    // Extra: all orders
    public List<Order> findAll() throws SQLException {
        String sql = "SELECT * FROM Orders ORDER BY order_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return toList(rs);
        }
    }

    // ---------- ResultSet -> Model ----------

    private List<Order> toList(ResultSet rs) throws SQLException {
        List<Order> list = new ArrayList<>();
        while (rs.next()) {
            list.add(toOrder(rs));
        }
        return list;
    }

    private Order toOrder(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("order_id"),
                rs.getString("customer_name"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getDouble("unit_price"),
                rs.getString("status"));
    }
}
