/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.databaseconnection;


import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.DriverManager;
import java.sql.Driver;
import java.util.Enumeration;


public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;

    private DatabaseConnection() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/syos_db");
            config.setUsername("root");
            config.setPassword("");
            config.setMaximumPoolSize(60);
            config.setMinimumIdle(5);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setLeakDetectionThreshold(2000);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            dataSource = new HikariDataSource(config);

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (dataSource != null) {
                        dataSource.close(); // Close connection pool
                    }
                    AbandonedConnectionCleanupThread.checkedShutdown(); // Cleanup MySQL threads

                 
                    Enumeration<Driver> drivers = DriverManager.getDrivers();
                    while (drivers.hasMoreElements()) {
                        DriverManager.deregisterDriver(drivers.nextElement());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the connection pool", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get a database connection", e);
        }
    }
}
