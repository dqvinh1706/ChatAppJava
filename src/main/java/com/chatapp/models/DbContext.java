package com.chatapp.models;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import java.sql.*;

public class DbContext {
    private static String DB_PREFIX = "jdbc:sqlserver://localhost";
    private static String DB_PORT = "1433";
    private static String DB_NAME = "Chatapp";
    private static String USERNAME = "sa";
    private static String PASSWORD = "dqv";

    private Connection conn;
    private Statement statement;

    public static DbContext getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final DbContext INSTANCE = new DbContext();
    }

    private void getConnection() {
        conn = null;
        String db_url = DB_PREFIX + ":" + DB_PORT + ";databaseName=" + DB_NAME + ";encrypt=true;trustServerCertificate=true;";
        try {
            DriverManager.registerDriver(new SQLServerDriver());
            conn = DriverManager.getConnection(db_url, USERNAME, PASSWORD);
            System.out.println("Connected DB");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private DbContext() {
        getConnection();
        try{
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("Select * from [user]");
            while(rs.next()) {
                System.out.println(rs.getArray(1));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
