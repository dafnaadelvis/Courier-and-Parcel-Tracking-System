package com.courier.dao;

import com.courier.model.Invoice;
import com.courier.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDao {

    private static final String SELECT = """
            SELECT i.*, p.tracking_number, u.full_name AS customer_name
            FROM invoices i
            JOIN parcels p ON p.id = i.parcel_id
            JOIN customers c ON c.id = p.customer_id
            JOIN users u ON u.id = c.user_id
            """;

    public Invoice findByParcelId(int parcelId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE i.parcel_id = ?")) {
            ps.setInt(1, parcelId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Invoice findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE i.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Invoice> findAll() throws SQLException {
        List<Invoice> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " ORDER BY i.id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Invoice> findByCustomerId(int customerId) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE p.customer_id = ? ORDER BY i.id DESC")) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public BigDecimal sumTotals() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(SUM(total),0) FROM invoices");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getBigDecimal(1);
        }
    }

    public int insert(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (parcel_id, amount, tax, total, status) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, invoice.getParcelId());
            ps.setBigDecimal(2, invoice.getAmount());
            ps.setBigDecimal(3, invoice.getTax());
            ps.setBigDecimal(4, invoice.getTotal());
            ps.setString(5, invoice.getStatus() == null ? "UNPAID" : invoice.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                invoice.setId(id);
                return id;
            }
        }
    }

    private Invoice map(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setId(rs.getInt("id"));
        i.setParcelId(rs.getInt("parcel_id"));
        i.setAmount(rs.getBigDecimal("amount"));
        i.setTax(rs.getBigDecimal("tax"));
        i.setTotal(rs.getBigDecimal("total"));
        Timestamp ts = rs.getTimestamp("generated_at");
        if (ts != null) {
            i.setGeneratedAt(ts.toLocalDateTime());
        }
        i.setStatus(rs.getString("status"));
        i.setTrackingNumber(rs.getString("tracking_number"));
        i.setCustomerName(rs.getString("customer_name"));
        return i;
    }
}
