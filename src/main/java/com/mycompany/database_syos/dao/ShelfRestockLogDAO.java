/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.dao;

/**
 *
 * @author User
 */

import com.mycompany.database_syos.databaseconnection.DatabaseConnection;
import com.mycompany.database_syos.models.ShelfRestockLog;
import com.mycompany.database_syos.exceptions.ShelfRestockLogException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ShelfRestockLogDAO {

    public ShelfRestockLogDAO() {
    }

   public void addRestockLog(ShelfRestockLog log) throws ShelfRestockLogException {
    String sql = "INSERT INTO shelf_restock_log (shelf_id, quantity, restock_timestamp) VALUES (?, ?, ?)";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, log.getShelfId());
        pstmt.setInt(2, log.getQuantity());
        pstmt.setTimestamp(3, log.getRestockTimestamp());
        pstmt.executeUpdate();

    } catch (SQLException e) {
        throw new ShelfRestockLogException("Failed to add restock log entry", e);
    }
}

   public List<ShelfRestockLog> getRestockLogsByShelfId(int shelfId) throws ShelfRestockLogException {
    List<ShelfRestockLog> restockLogs = new ArrayList<>();
    String sql = "SELECT * FROM shelf_restock_log WHERE shelf_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, shelfId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ShelfRestockLog log = new ShelfRestockLog();
                log.setRestockLogId(rs.getInt("restock_log_id"));
                log.setShelfId(rs.getInt("shelf_id"));
                log.setQuantity(rs.getInt("quantity"));
                log.setRestockTimestamp(rs.getTimestamp("restock_timestamp"));
                restockLogs.add(log);
            }
        }

    } catch (SQLException e) {
        throw new ShelfRestockLogException("Error retrieving restock logs from the database.", e);
    }

    return restockLogs;
}

}

