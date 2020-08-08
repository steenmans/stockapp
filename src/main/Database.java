package main;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    private Connection conn;
    private boolean conn_status = false;

    //Setter method for connection
    public void dbConnection() {

        try {
            //Register JDBC Driver
            Class.forName("com.mysql.jdbc.Driver");

            //Open a connection
            conn = DriverManager.getConnection("jdbc:mysql://192.168.1.114:3306/stock?autoReconnect=true&useSSL=false", "steenmans", "Marlboro5419!");

            conn_status = true;
        } catch (Exception e) {
            e.printStackTrace();

            conn_status = false;
        }

    }

    //Getters

    public boolean isConn_status() {
        return conn_status;
    }

    //Getter method for connection
    public Connection getConn() {
        return this.conn;
    }
}
