package com.courier.dao;

import com.courier.model.Parcel;
import com.courier.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParcelDao {

    private static final String SELECT = """
            SELECT p.*, u.full_name AS customer_name,
                   (SELECT u2.full_name FROM assignments asg
                    JOIN delivery_agents da ON da.id = asg.agent_id
                    JOIN users u2 ON u2.id = da.user_id
                    WHERE asg.parcel_id = p.id
                    ORDER BY asg.id DESC LIMIT 1) AS agent_name
            FROM parcels p
            JOIN customers c ON c.id = p.customer_id
            JOIN users u ON u.id = c.user_id
            """;

    public Parcel findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE p.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Parcel findByTrackingNumber(String trackingNumber) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE p.tracking_number = ?")) {
            ps.setString(1, trackingNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Parcel> findAll() throws SQLException {
        List<Parcel> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " ORDER BY p.id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Parcel> findByCustomerId(int customerId) throws SQLException {
        List<Parcel> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE p.customer_id = ? ORDER BY p.id DESC")) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Parcel> findUnassigned() throws SQLException {
        List<Parcel> list = new ArrayList<>();
        String sql = SELECT + """
                WHERE p.id NOT IN (SELECT parcel_id FROM assignments)
                AND p.status NOT IN ('DELIVERED', 'CANCELLED')
                ORDER BY p.id DESC
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

    public List<Parcel> findAssignedToAgent(int agentId) throws SQLException {
        List<Parcel> list = new ArrayList<>();
        String sql = SELECT + """
                JOIN assignments asg ON asg.parcel_id = p.id
                WHERE asg.agent_id = ?
                ORDER BY p.id DESC
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, agentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public Map<String, Integer> countByStatus() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) cnt FROM parcels GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("cnt"));
            }
        }
        return map;
    }

    public int insert(Parcel parcel) throws SQLException {
        String sql = """
                INSERT INTO parcels (tracking_number, customer_id, sender_name, sender_address,
                    receiver_name, receiver_address, receiver_phone, weight_kg, parcel_type, status)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, parcel.getTrackingNumber());
            ps.setInt(2, parcel.getCustomerId());
            ps.setString(3, parcel.getSenderName());
            ps.setString(4, parcel.getSenderAddress());
            ps.setString(5, parcel.getReceiverName());
            ps.setString(6, parcel.getReceiverAddress());
            ps.setString(7, parcel.getReceiverPhone());
            ps.setBigDecimal(8, parcel.getWeightKg());
            ps.setString(9, parcel.getParcelType());
            ps.setString(10, parcel.getStatus() == null ? "CREATED" : parcel.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                parcel.setId(id);
                return id;
            }
        }
    }

    public void update(Parcel parcel) throws SQLException {
        String sql = """
                UPDATE parcels SET sender_name=?, sender_address=?, receiver_name=?, receiver_address=?,
                    receiver_phone=?, weight_kg=?, parcel_type=?, status=? WHERE id=?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, parcel.getSenderName());
            ps.setString(2, parcel.getSenderAddress());
            ps.setString(3, parcel.getReceiverName());
            ps.setString(4, parcel.getReceiverAddress());
            ps.setString(5, parcel.getReceiverPhone());
            ps.setBigDecimal(6, parcel.getWeightKg());
            ps.setString(7, parcel.getParcelType());
            ps.setString(8, parcel.getStatus());
            ps.setInt(9, parcel.getId());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE parcels SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Parcel map(ResultSet rs) throws SQLException {
        Parcel p = new Parcel();
        p.setId(rs.getInt("id"));
        p.setTrackingNumber(rs.getString("tracking_number"));
        p.setCustomerId(rs.getInt("customer_id"));
        p.setSenderName(rs.getString("sender_name"));
        p.setSenderAddress(rs.getString("sender_address"));
        p.setReceiverName(rs.getString("receiver_name"));
        p.setReceiverAddress(rs.getString("receiver_address"));
        p.setReceiverPhone(rs.getString("receiver_phone"));
        p.setWeightKg(rs.getBigDecimal("weight_kg"));
        p.setParcelType(rs.getString("parcel_type"));
        p.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            p.setCreatedAt(ts.toLocalDateTime());
        }
        p.setCustomerName(rs.getString("customer_name"));
        p.setAssignedAgentName(rs.getString("agent_name"));
        return p;
    }
}
