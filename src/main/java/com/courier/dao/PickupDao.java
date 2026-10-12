package com.courier.dao;

import com.courier.model.Pickup;
import com.courier.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class PickupDao {

    private static final String SELECT = """
            SELECT pk.*, p.tracking_number, u.full_name AS customer_name
            FROM pickups pk
            JOIN parcels p ON p.id = pk.parcel_id
            JOIN customers c ON c.id = p.customer_id
            JOIN users u ON u.id = c.user_id
            """;

    public List<Pickup> findAll() throws SQLException {
        List<Pickup> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " ORDER BY pk.scheduled_date, pk.scheduled_time");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Pickup> findByCustomerId(int customerId) throws SQLException {
        List<Pickup> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE p.customer_id = ? ORDER BY pk.id DESC")) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int countByDate(java.time.LocalDate date) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM pickups WHERE scheduled_date = ?")) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int insert(Pickup pickup) throws SQLException {
        String sql = "INSERT INTO pickups (parcel_id, scheduled_date, scheduled_time, pickup_address, status) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pickup.getParcelId());
            ps.setDate(2, Date.valueOf(pickup.getScheduledDate()));
            ps.setTime(3, Time.valueOf(pickup.getScheduledTime()));
            ps.setString(4, pickup.getPickupAddress());
            ps.setString(5, pickup.getStatus() == null ? "SCHEDULED" : pickup.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                pickup.setId(id);
                return id;
            }
        }
    }

    private Pickup map(ResultSet rs) throws SQLException {
        Pickup p = new Pickup();
        p.setId(rs.getInt("id"));
        p.setParcelId(rs.getInt("parcel_id"));
        Date d = rs.getDate("scheduled_date");
        if (d != null) {
            p.setScheduledDate(d.toLocalDate());
        }
        Time t = rs.getTime("scheduled_time");
        if (t != null) {
            p.setScheduledTime(t.toLocalTime());
        }
        p.setPickupAddress(rs.getString("pickup_address"));
        p.setStatus(rs.getString("status"));
        p.setTrackingNumber(rs.getString("tracking_number"));
        p.setCustomerName(rs.getString("customer_name"));
        return p;
    }
}
