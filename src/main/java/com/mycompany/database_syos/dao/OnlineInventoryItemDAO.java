/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.dao;


import com.mycompany.database_syos.databaseconnection.DatabaseConnection;
import com.mycompany.database_syos.exceptions.OnlineInventoryDatabaseException;
import com.mycompany.database_syos.models.OnlineInventoryItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author User
 */

public class OnlineInventoryItemDAO {
    
    public OnlineInventoryItemDAO() {
    }
    
    private final ReentrantLock inventoryLock = new ReentrantLock(); 

    public void addOnlineInventoryItem(OnlineInventoryItem onlineInventoryItem) throws OnlineInventoryDatabaseException {
        String sql = "INSERT INTO online_inventory_items (online_inventory_id, batch_id, item_serial_number, product_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection(); // Get connection from HikariCP
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, onlineInventoryItem.getOnlineInventoryId());
            pstmt.setInt(2, onlineInventoryItem.getBatchId());
            pstmt.setString(3, onlineInventoryItem.getItemSerialNumber());
            pstmt.setInt(4, onlineInventoryItem.getProductId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new OnlineInventoryDatabaseException("Error adding online inventory item to the database.", e);
        }
    }

   public List<OnlineInventoryItem> getOnlineInventoryItemsForProduct(int productId) throws SQLException {
    String sql = "SELECT * FROM online_inventory_items WHERE product_id = ? ORDER BY online_inventory_item_id ASC";
    List<OnlineInventoryItem> onlineInventoryItems = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); // Get a fresh connection from HikariCP
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, productId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                onlineInventoryItems.add(new OnlineInventoryItem(
                    rs.getInt("online_inventory_item_id"), 
                    rs.getInt("online_inventory_id"),
                    rs.getString("item_serial_number"), 
                    rs.getInt("batch_id"), 
                    rs.getInt("product_id")
                ));
            }
        }
    } catch (SQLException e) {
        throw new SQLException("Error retrieving inventory items for product ID: " + productId, e);
    }
    return onlineInventoryItems;
}


   public void removeOnlineInventoryItem(int onlineInventoryItemId) throws SQLException {
    String sql = "DELETE FROM online_inventory_items WHERE online_inventory_item_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); // fresh connection from HikariCP
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, onlineInventoryItemId);
        int affectedRows = pstmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("No inventory item found with ID: " + onlineInventoryItemId);
        }

        System.out.println("Successfully removed inventory item with ID: " + onlineInventoryItemId);

    } catch (SQLException e) {
        throw new SQLException("Error removing inventory item with ID: " + onlineInventoryItemId, e);
    }
}

   public Optional<OnlineInventoryItem> getOnlineInventoryItemBySerialNumber(String itemSerialNumber) throws SQLException {
    String sql = "SELECT * FROM online_inventory_items WHERE item_serial_number = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setString(1, itemSerialNumber);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(new OnlineInventoryItem(
                    rs.getInt("online_inventory_item_id"), 
                    rs.getInt("online_inventory_id"), 
                    rs.getString("item_serial_number"), 
                    rs.getInt("batch_id"), 
                    rs.getInt("product_id")
                ));
            }
        }
    } catch (SQLException e) {
        throw new SQLException("Error retrieving online inventory item with serial number: " + itemSerialNumber, e);
    }
    return Optional.empty(); 
}
    
   public void reduceStockByQuantity(int onlineInventoryItemId, int quantity) throws SQLException {  
    String checkQuery = "SELECT quantity FROM online_inventory_items WHERE online_inventory_item_id = ?";
    String updateQuery = "UPDATE online_inventory_items SET quantity = quantity - ? WHERE online_inventory_item_id = ?";
    
    Connection connection = null;

    try {
        connection = DatabaseConnection.getInstance().getConnection();
        connection.setAutoCommit(false); // Start transaction

        // Check current stock level
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, onlineInventoryItemId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int currentQuantity = rs.getInt("quantity");
                    if (currentQuantity < quantity) {
                        throw new SQLException("Insufficient stock for inventory item ID: " + onlineInventoryItemId);
                    }
                } else {
                    throw new SQLException("No inventory item found with ID: " + onlineInventoryItemId);
                }
            }
        }

        // Reduce stock
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, quantity);
            updateStmt.setInt(2, onlineInventoryItemId);
            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Stock reduction failed: No matching inventory item found.");
            }
        }

        connection.commit(); // Commit transaction
        System.out.println("Stock successfully reduced for inventory item ID: " + onlineInventoryItemId);

    } catch (SQLException e) {
        if (connection != null) {
            connection.rollback(); // Rollback if error occurs
            System.out.println("Transaction rolled back due to error.");
        }
        throw new SQLException("Error reducing stock for inventory item ID: " + onlineInventoryItemId, e);

    } finally {
        if (connection != null) {
            connection.setAutoCommit(true);
            connection.close();
        }
    }
}
   
    public void reduceStockByBatchId(int batchId, int quantity) throws SQLException {
            inventoryLock.lock(); // Acquire lock for thread safety
            Connection connection = null;

            try {
                connection = DatabaseConnection.getInstance().getConnection();
                connection.setAutoCommit(false); // Start transaction

                // Check stock availability
                String checkQuery = "SELECT current_quantity FROM online_inventory oi " +
                                    "JOIN online_inventory_items oi_items ON oi.online_inventory_id = oi_items.online_inventory_id " +
                                    "WHERE oi_items.batch_id = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, batchId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            int currentQuantity = rs.getInt("current_quantity");
                            if (currentQuantity < quantity) {
                                throw new SQLException("Insufficient stock for batch ID: " + batchId);
                            }
                        } else {
                            throw new SQLException("Batch ID " + batchId + " does not exist.");
                        }
                    }
                }

                // Reduce stock
                String updateQuery = "UPDATE online_inventory oi " +
                                     "JOIN online_inventory_items oi_items ON oi.online_inventory_id = oi_items.online_inventory_id " +
                                     "SET oi.current_quantity = oi.current_quantity - ? " +
                                     "WHERE oi_items.batch_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, quantity); 
                    pstmt.setInt(2, batchId); 
                    int rowsUpdated = pstmt.executeUpdate();

                    if (rowsUpdated == 0) {
                        throw new SQLException("Stock reduction failed: No matching batch ID found.");
                    }
                }

                connection.commit(); // Commit transaction
                System.out.println("Successfully reduced stock for batch ID: " + batchId);

            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback(); // Rollback if error occurs
                    System.out.println("Transaction rolled back due to error.");
                }
                throw new SQLException("Error reducing stock for batch ID: " + batchId, e);

            } finally {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
                inventoryLock.unlock(); // Release lock
            }
        }
    
    public int getAvailableBatchId(int productId, int requiredQuantity) throws SQLException {
        String query = """
            SELECT oi_items.batch_id
            FROM online_inventory_items oi_items
            JOIN online_inventory oi ON oi.online_inventory_id = oi_items.online_inventory_id
            WHERE oi.product_id = ? AND oi.current_quantity >= ?
            ORDER BY oi_items.batch_id ASC  -- Ensures the oldest batch is used first
            LIMIT 1
        """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, productId);     
            pstmt.setInt(2, requiredQuantity);  

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("batch_id"); 
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving batch ID for product ID: " + productId + ", Required Quantity: " + requiredQuantity, e);
        }
        return -1; 
    }

    public void removeItemsForProduct(int productId, int batchId, int quantity) throws SQLException, InterruptedException {
        boolean acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS);
        if (!acquired) {
            throw new SQLException("Could not acquire lock. Try again later.");
        }

        Connection connection = null;
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Select the exact rows to delete
            String selectQuery = "SELECT online_inventory_item_id FROM online_inventory_items WHERE product_id = ? AND batch_id = ? ORDER BY online_inventory_item_id ASC LIMIT ?";
            List<Integer> itemIds = new ArrayList<>();

            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, productId);
                selectStmt.setInt(2, batchId);
                selectStmt.setInt(3, quantity);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        itemIds.add(rs.getInt("online_inventory_item_id"));
                    }
                }
            }

            if (itemIds.size() < quantity) {
                throw new SQLException("Not enough serialized items to remove.");
            }

            //Delete the selected rows
            String deleteQuery = "DELETE FROM online_inventory_items WHERE online_inventory_item_id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                for (int itemId : itemIds) {
                    deleteStmt.setInt(1, itemId);
                    deleteStmt.executeUpdate();
                }
            }

            connection.commit();
            System.out.println("Successfully removed " + quantity + " items for product ID: " + productId + " from batch ID: " + batchId);
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
            inventoryLock.unlock();
        }
    }

}