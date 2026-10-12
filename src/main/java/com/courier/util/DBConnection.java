package com.courier.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("jdbc.url"),
                PROPS.getProperty("jdbc.user"),
                PROPS.getProperty("jdbc.password"));
    }

    public static Connection getBootstrapConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("jdbc.bootstrapUrl"),
                PROPS.getProperty("jdbc.user"),
                PROPS.getProperty("jdbc.password"));
    }

    public static String user() {
        return PROPS.getProperty("jdbc.user");
    }

    public static String password() {
        return PROPS.getProperty("jdbc.password");
    }
}
