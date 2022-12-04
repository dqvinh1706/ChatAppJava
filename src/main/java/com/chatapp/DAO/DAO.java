package com.chatapp.DAO;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DAO<T> {
    private static String DB_PREFIX = "jdbc:sqlserver://localhost";
    private static String DB_PORT = "1433";
    private static String DB_NAME = "Chatapp";
    private static String USERNAME = "sa";
    private static String PASSWORD = "dqv1";

    public Connection getConnection() {
        Connection conn = null;
        String db_url = DB_PREFIX + ":" + DB_PORT + ";databaseName=" + DB_NAME + ";encrypt=true;trustServerCertificate=true;";
        try {
            DriverManager.registerDriver(new SQLServerDriver());
            conn = DriverManager.getConnection(db_url, USERNAME, PASSWORD);
            System.out.println("Connected DB");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return conn;
    }

    public PreparedStatement setParameter(PreparedStatement preparedStatement, Object... parameters) throws SQLException {
        int size = parameters.length;
        for (int i = 0; i < size; i++) {
            Object param = parameters[i];
            if (param instanceof String) {
                preparedStatement.setString(i, (String) param);
            }
            else if (param instanceof Integer) {
                preparedStatement.setInt(i, (Integer) param);
            }
            else if (param instanceof Boolean) {
                preparedStatement.setBoolean(i, (Boolean) param);
            }
            else if (param instanceof Blob) {
                preparedStatement.setBlob(i, (Blob) param);
            }
        }
        return preparedStatement;
    }


    public abstract List<T> parse(ResultSet rs);

    protected List<T> executeQuery(String sql, Object... parameters) {
        List<T> result = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        try{
            conn = getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement = setParameter(preparedStatement, parameters);
            rs = preparedStatement.executeQuery();
            result = parse(rs);
            conn.commit();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
            return null;
        } finally {
            try {
                if (conn != null ) conn.close();
                if (preparedStatement != null ) preparedStatement.close();
                if (rs != null ) rs.close();
            } catch (SQLException err) {}
        }

        return result;
    }

    protected int executeUpdate(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        int rows = -1;
        try {
            preparedStatement = conn.prepareStatement(sql);
            setParameter(preparedStatement, parameters);
            rows = preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException err) {
            try{ conn.rollback(); } catch (SQLException e) {};
        } finally {
            try {
                if (conn != null ) conn.close();
                if (preparedStatement != null ) preparedStatement.close();
            } catch (SQLException err) {}
        }
        return rows;
    }
}
