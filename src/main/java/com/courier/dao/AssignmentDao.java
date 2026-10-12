package com.courier.dao;

import com.courier.model.Assignment;
import com.courier.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDao {

    public List<Assignment> findAll() throws SQLException {
        List<Assignment> list = new ArrayList<>();
        String sql = """
                SELECT asg.*, p.tracking_number, u.full_name AS agent_name
                FROM assignments asg
                JOIN parcels p ON p.id = asg.parcel_id
                JOIN delivery_agents da ON da.id = asg.agent_id
                JOIN users u ON u.id = da.user_id
                ORDER BY asg.id DESC
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

    public boolean existsForParcel(int parcelId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM assignments WHERE parcel_id=?")) {
            ps.setInt(1, parcelId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int insert(Assignment assignment) throws SQLException {
        String sql = "INSERT INTO assignments (parcel_id, agent_id) VALUES (?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, assignment.getParcelId());
            ps.setInt(2, assignment.getAgentId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                assignment.setId(id);
                return id;
            }
        }
    }

    private Assignment map(ResultSet rs) throws SQLException {
        Assignment a = new Assignment();
        a.setId(rs.getInt("id"));
        a.setParcelId(rs.getInt("parcel_id"));
        a.setAgentId(rs.getInt("agent_id"));
        Timestamp ts = rs.getTimestamp("assigned_at");
        if (ts != null) {
            a.setAssignedAt(ts.toLocalDateTime());
        }
        a.setTrackingNumber(rs.getString("tracking_number"));
        a.setAgentName(rs.getString("agent_name"));
        return a;
    }
}
