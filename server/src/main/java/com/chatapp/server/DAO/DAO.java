package com.chatapp.server.DAO;

import com.chatapp.commons.utils.TimestampUtil;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import lombok.Synchronized;

import java.sql.*;
import java.util.Date;
import java.util.List;

public abstract class DAO<T> {
    private static String DB_PREFIX = "jdbc:sqlserver://localhost\\SQLEXPRESS";
    private static String DB_PORT = "1433";
    private static String DB_NAME = "Chatapp";
    private static String USERNAME = "sa";
    private static String PASSWORD = "Thq17062002";

    @Synchronized
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
        for (int i = 1; i <= size; i++) {
            Object param = parameters[i - 1];

            if (param instanceof Timestamp) {
                preparedStatement.setString(i, TimestampUtil.datetimeFormat((Timestamp) param));
            }
            else if (param instanceof String) {
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
            else if (param instanceof Date) {
                preparedStatement.setDate(i, new java.sql.Date(((Date) param).getTime()));
            }
        }
        return preparedStatement;
    }


    public abstract List<T> parse(ResultSet rs);

    @Synchronized
    protected List<T> executeQuery(String sql, Object... parameters) {
        List<T> result = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement = setParameter(preparedStatement, parameters);
            rs = preparedStatement.executeQuery();
            result = parse(rs);
            conn.commit();
        } catch (SQLException err) {
            err.printStackTrace();
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

    @Synchronized
    protected Long executeUpdate(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParameter(preparedStatement, parameters);
            preparedStatement.executeUpdate();
            rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                conn.commit();
                return rs.getLong(1);
            }
        } catch (Exception err) {
            err.printStackTrace();
            try{ conn.rollback(); }
            catch (SQLException e) {
                e.printStackTrace();
            };
        } finally {
            try {
                if (conn != null ) conn.close();
                if (preparedStatement != null ) preparedStatement.close();
                if (rs != null) rs.close();
            } catch (SQLException err) {}
        }
        return null;
    }
}
