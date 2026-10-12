package com.courier.dao;

import com.courier.model.Customer;
import com.courier.model.User;
import com.courier.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    public Customer findByUserId(int userId) throws SQLException {
        String sql = """
                SELECT c.*, u.username, u.full_name, u.email, u.phone, u.role
                FROM customers c JOIN users u ON u.id = c.user_id
                WHERE c.user_id = ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Customer findById(int id) throws SQLException {
        String sql = """
                SELECT c.*, u.username, u.full_name, u.email, u.phone, u.role
                FROM customers c JOIN users u ON u.id = c.user_id
                WHERE c.id = ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = """
                SELECT c.*, u.username, u.full_name, u.email, u.phone, u.role
                FROM customers c JOIN users u ON u.id = c.user_id
                ORDER BY c.id
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public int insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (user_id, address, city, pincode) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customer.getUserId());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getCity());
            ps.setString(4, customer.getPincode());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                customer.setId(id);
                return id;
            }
        }
    }

    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET address=?, city=?, pincode=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getAddress());
            ps.setString(2, customer.getCity());
            ps.setString(3, customer.getPincode());
            ps.setInt(4, customer.getId());
            ps.executeUpdate();
        }
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setAddress(rs.getString("address"));
        c.setCity(rs.getString("city"));
        c.setPincode(rs.getString("pincode"));
        User u = new User();
        u.setId(c.getUserId());
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        c.setUser(u);
        return c;
    }
}
