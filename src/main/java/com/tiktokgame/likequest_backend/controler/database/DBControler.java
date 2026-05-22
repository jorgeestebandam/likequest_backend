package com.tiktokgame.likequest_backend.controler.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBControler {


    public Connection connectDB() throws SQLException {
        Connection conexion;
        String url = "jdbc:mysql://localhost:3306/likequest";
        String user = "root";
        String password = "root";
        conexion = DriverManager.getConnection(url,user,password);
        return conexion;
    }

    public void  closeDBConnection(Connection conexion) throws SQLException {
        conexion.close();
    }
}
