package com.courier.dao;

import com.courier.model.DeliveryAgent;
import com.courier.model.User;
import com.courier.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DeliveryAgentDao {

    public DeliveryAgent findByUserId(int userId) throws SQLException {
        String sql = """
                SELECT a.*, u.username, u.full_name, u.email, u.phone, u.role
                FROM delivery_agents a JOIN users u ON u.id = a.user_id
                WHERE a.user_id = ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public DeliveryAgent findById(int id) throws SQLException {
        String sql = """
                SELECT a.*, u.username, u.full_name, u.email, u.phone, u.role
                FROM delivery_agents a JOIN users u ON u.id = a.user_id
                WHERE a.id = ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<DeliveryAgent> findAll() throws SQLException {
        List<DeliveryAgent> list = new ArrayList<>();
        String sql = """
                SELECT a.*, u.username, u.full_name, u.email, u.phone, u.role
                FROM delivery_agents a JOIN users u ON u.id = a.user_id
                ORDER BY a.id
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

    public int insert(DeliveryAgent agent) throws SQLException {
        String sql = "INSERT INTO delivery_agents (user_id, vehicle_no, status) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, agent.getUserId());
            ps.setString(2, agent.getVehicleNo());
            ps.setString(3, agent.getStatus() == null ? "AVAILABLE" : agent.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                agent.setId(id);
                return id;
            }
        }
    }

    public void update(DeliveryAgent agent) throws SQLException {
        String sql = "UPDATE delivery_agents SET vehicle_no=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, agent.getVehicleNo());
            ps.setString(2, agent.getStatus());
            ps.setInt(3, agent.getId());
            ps.executeUpdate();
        }
    }

    private DeliveryAgent map(ResultSet rs) throws SQLException {
        DeliveryAgent a = new DeliveryAgent();
        a.setId(rs.getInt("id"));
        a.setUserId(rs.getInt("user_id"));
        a.setVehicleNo(rs.getString("vehicle_no"));
        a.setStatus(rs.getString("status"));
        User u = new User();
        u.setId(a.getUserId());
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        a.setUser(u);
        return a;
    }
}
