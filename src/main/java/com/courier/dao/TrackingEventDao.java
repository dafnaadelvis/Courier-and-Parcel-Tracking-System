package com.courier.dao;

import com.courier.model.TrackingEvent;
import com.courier.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TrackingEventDao {

    public List<TrackingEvent> findByParcelId(int parcelId) throws SQLException {
        List<TrackingEvent> list = new ArrayList<>();
        String sql = """
                SELECT t.*, u.full_name AS updated_by_name
                FROM tracking_events t
                LEFT JOIN users u ON u.id = t.updated_by
                WHERE t.parcel_id = ?
                ORDER BY t.updated_at
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parcelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public void insert(TrackingEvent event) throws SQLException {
        String sql = "INSERT INTO tracking_events (parcel_id, status, location, remarks, updated_by) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, event.getParcelId());
            ps.setString(2, event.getStatus());
            ps.setString(3, event.getLocation());
            ps.setString(4, event.getRemarks());
            if (event.getUpdatedBy() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, event.getUpdatedBy());
            }
            ps.executeUpdate();
        }
    }

    private TrackingEvent map(ResultSet rs) throws SQLException {
        TrackingEvent e = new TrackingEvent();
        e.setId(rs.getInt("id"));
        e.setParcelId(rs.getInt("parcel_id"));
        e.setStatus(rs.getString("status"));
        e.setLocation(rs.getString("location"));
        e.setRemarks(rs.getString("remarks"));
        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) {
            e.setUpdatedAt(ts.toLocalDateTime());
        }
        int uid = rs.getInt("updated_by");
        e.setUpdatedBy(rs.wasNull() ? null : uid);
        e.setUpdatedByName(rs.getString("updated_by_name"));
        return e;
    }
}
