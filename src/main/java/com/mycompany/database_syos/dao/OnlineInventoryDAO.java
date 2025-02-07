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
import com.mycompany.database_syos.models.OnlineInventory;
import com.mycompany.database_syos.exceptions.OnlineInventoryDatabaseException;
import com.mycompany.database_syos.models.OnlineInventoryItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class OnlineInventoryDAO {
    private final ReentrantLock inventoryLock = new ReentrantLock(); 

    public OnlineInventoryDAO() { }
  
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }


    public void addOnlineInventory(OnlineInventory onlineInventory) throws OnlineInventoryDatabaseException, InterruptedException {
      boolean acquired = false;
      try {
          acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS); 
          if (!acquired) {
              throw new OnlineInventoryDatabaseException("Could not acquire lock. Try again later.");
          }

          if (onlineInventory.getCurrentQuantity() > onlineInventory.getMaxCapacity()) {
              throw new OnlineInventoryDatabaseException("Current quantity cannot exceed max capacity.");
          }

          String sql = "INSERT INTO online_inventory (product_id, max_capacity, current_quantity, restock_threshold, timestamp) VALUES (?, ?, ?, ?, ?)";

          try (Connection connection = DatabaseConnection.getInstance().getConnection();
               PreparedStatement pstmt = connection.prepareStatement(sql)) {
              connection.setAutoCommit(false); 

              pstmt.setInt(1, onlineInventory.getProductId());
              pstmt.setInt(2, onlineInventory.getMaxCapacity());
              pstmt.setInt(3, onlineInventory.getCurrentQuantity());
              pstmt.setInt(4, onlineInventory.getRestockThreshold());
              pstmt.setTimestamp(5, onlineInventory.getTimestamp());
              pstmt.executeUpdate();

              connection.commit(); 
          } catch (SQLException e) {
              throw new OnlineInventoryDatabaseException("Error adding online inventory to the database.", e);
          }

      } finally {
          if (acquired) {
              inventoryLock.unlock(); 
          }
      }
    }
  
    public void addOnlineInventoryItem(OnlineInventoryItem onlineInventoryItem) throws OnlineInventoryDatabaseException {
       inventoryLock.lock(); // Prevents concurrent inserts
       boolean acquired = false;

       String sql = """
           INSERT INTO online_inventory_items (online_inventory_id, batch_id, item_serial_number, product_id) 
           VALUES (?, ?, ?, ?)
           ON DUPLICATE KEY UPDATE batch_id = VALUES(batch_id), item_serial_number = VALUES(item_serial_number)
       """;

       try {
           acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS); 
           if (!acquired) {
               throw new OnlineInventoryDatabaseException("Could not acquire lock. Try again later.");
           }

           try (Connection connection = DatabaseConnection.getInstance().getConnection()) { // fresh connection
               connection.setAutoCommit(false);

               try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                   pstmt.setInt(1, onlineInventoryItem.getOnlineInventoryId());
                   pstmt.setInt(2, onlineInventoryItem.getBatchId());
                   pstmt.setString(3, onlineInventoryItem.getItemSerialNumber());
                   pstmt.setInt(4, onlineInventoryItem.getProductId());
                   pstmt.executeUpdate();
               }

               connection.commit(); 
           }

       } catch (InterruptedException e) {
           Thread.currentThread().interrupt(); 
           throw new OnlineInventoryDatabaseException("Thread interrupted while acquiring lock.", e);
       } catch (SQLException e) {
           throw new OnlineInventoryDatabaseException("Error adding item to online inventory.", e);
       } finally {
           if (acquired) {
               inventoryLock.unlock();
           }
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
        throw new SQLException("Error retrieving online inventory item by serial number: " + itemSerialNumber, e);
    }
    return Optional.empty(); 
}

    public boolean removeOnlineInventoryItem(int onlineInventoryItemId) throws OnlineInventoryDatabaseException {
    boolean acquired = false;
    inventoryLock.lock(); 

    String sql = "DELETE FROM online_inventory_items WHERE online_inventory_item_id = ?";
    
    try {
        acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS); 
        if (!acquired) {
            throw new OnlineInventoryDatabaseException("Could not acquire lock. Try again later.");
        }

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) { 
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, onlineInventoryItemId);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new OnlineInventoryDatabaseException("No inventory item found with ID: " + onlineInventoryItemId);
                }

                connection.commit();
                return true;
            }
        }

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); 
        throw new OnlineInventoryDatabaseException("Thread interrupted while acquiring lock.", e);
    } catch (SQLException e) {
        throw new OnlineInventoryDatabaseException("Error deleting online inventory item.", e);
    } finally {
        if (acquired) {
            inventoryLock.unlock(); 
        }
    }
}


   public void displayAllProducts() {
    Logger logger = Logger.getLogger(OnlineInventoryDAO.class.getName());
    String sql = "SELECT product_id, name FROM product LIMIT 100"; 

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        StringBuilder output = new StringBuilder();
        output.append("Product ID | Product Name\n");
        output.append("------------------------\n");

        while (rs.next()) {
            int productId = rs.getInt("product_id");
            String productName = rs.getString("name");
            output.append(productId).append(" | ").append(productName).append("\n");
        }

        logger.info(output.toString()); 

    } catch (SQLException e) {
        logger.severe("Error retrieving product list: " + e.getMessage()); 
    }
}
   public List<OnlineInventory> getAllOnlineInventories() throws OnlineInventoryDatabaseException {
    Logger logger = Logger.getLogger(OnlineInventoryDAO.class.getName()); 
    List<OnlineInventory> onlineInventories = new ArrayList<>();
    String sql = "SELECT * FROM online_inventory LIMIT 100"; 

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            onlineInventories.add(new OnlineInventory(
                rs.getInt("online_inventory_id"),
                rs.getInt("product_id"),
                rs.getInt("max_capacity"),
                rs.getInt("current_quantity"),
                rs.getInt("restock_threshold"),
                rs.getTimestamp("timestamp")
            ));
        }

        logger.info("Successfully retrieved " + onlineInventories.size() + " inventory records."); 

    } catch (SQLException e) {
        logger.severe("Error retrieving online inventories: " + e.getMessage()); 
        throw new OnlineInventoryDatabaseException("Error retrieving online inventories from the database.", e);
    }
    return onlineInventories;
}

    public OnlineInventory findOnlineInventoryById(int onlineInventoryId) throws OnlineInventoryDatabaseException {
        Logger logger = Logger.getLogger(OnlineInventoryDAO.class.getName()); 
        String sql = "SELECT * FROM online_inventory WHERE online_inventory_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, onlineInventoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new OnlineInventory(
                        rs.getInt("online_inventory_id"),
                        rs.getInt("product_id"),
                        rs.getInt("max_capacity"),
                        rs.getInt("current_quantity"),
                        rs.getInt("restock_threshold"),
                        rs.getTimestamp("timestamp")
                    );
                }
            }
        } catch (SQLException e) {
            logger.severe("Error finding online inventory by ID: " + onlineInventoryId + " - " + e.getMessage()); 
            throw new OnlineInventoryDatabaseException("Error finding online inventory by ID.", e);
        }

        return null; 
    }

    public boolean updateOnlineInventory(OnlineInventory onlineInventory) throws OnlineInventoryDatabaseException {
    boolean acquired = false;
    inventoryLock.lock(); //Prevent concurrent updates

    String sql = """
        UPDATE online_inventory 
        SET product_id = ?, max_capacity = ?, current_quantity = ?, restock_threshold = ?, timestamp = ? 
        WHERE online_inventory_id = ?
    """;

    try {
        acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS); 
        if (!acquired) {
            throw new OnlineInventoryDatabaseException("Could not acquire lock. Try again later.");
        }

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) { // Get fresh connection
            connection.setAutoCommit(false); 

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, onlineInventory.getProductId());
                pstmt.setInt(2, onlineInventory.getMaxCapacity());
                pstmt.setInt(3, onlineInventory.getCurrentQuantity());
                pstmt.setInt(4, onlineInventory.getRestockThreshold());
                pstmt.setTimestamp(5, onlineInventory.getTimestamp());
                pstmt.setInt(6, onlineInventory.getOnlineInventoryId());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new OnlineInventoryDatabaseException("No inventory record found with ID: " + onlineInventory.getOnlineInventoryId());
                }

                connection.commit(); 
                return true;
            }
        }

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); 
        throw new OnlineInventoryDatabaseException("Thread interrupted while acquiring lock.", e);
    } catch (SQLException e) {
        throw new OnlineInventoryDatabaseException("Error updating online inventory.", e);
    } finally {
        if (acquired) {
            inventoryLock.unlock(); 
        }
    }
}

    public boolean deleteOnlineInventory(int onlineInventoryId) throws OnlineInventoryDatabaseException {
        boolean acquired = false;
        inventoryLock.lock();

        String sql = "DELETE FROM online_inventory WHERE online_inventory_id = ?";

        try {
            acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS); 
            if (!acquired) {
                throw new OnlineInventoryDatabaseException("Could not acquire lock. Try again later.");
            }

            try (Connection connection = DatabaseConnection.getInstance().getConnection()) { 
                connection.setAutoCommit(false); 

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, onlineInventoryId);
                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows == 0) {
                        throw new OnlineInventoryDatabaseException("No inventory record found with ID: " + onlineInventoryId);
                    }

                    connection.commit(); 
                    return true;
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OnlineInventoryDatabaseException("Thread interrupted while acquiring lock.", e);
        } catch (SQLException e) {
            throw new OnlineInventoryDatabaseException("Error deleting online inventory.", e);
        } finally {
            if (acquired) {
                inventoryLock.unlock(); 
            }
        }
    }
    
    public boolean updateOnlineInventoryQuantity(int onlineInventoryId, int newQuantity) throws OnlineInventoryDatabaseException {
        boolean acquired = false;
        inventoryLock.lock();

        String sql = "UPDATE online_inventory SET current_quantity = ? WHERE online_inventory_id = ?";

        try {
            acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS);
            if (!acquired) {
                throw new OnlineInventoryDatabaseException("Could not acquire lock. Try again later.");
            }

            try (Connection connection = DatabaseConnection.getInstance().getConnection()) { 
                connection.setAutoCommit(false); 

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, newQuantity);
                    pstmt.setInt(2, onlineInventoryId);

                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new OnlineInventoryDatabaseException("No inventory record found with ID: " + onlineInventoryId);
                    }

                    connection.commit(); 
                    return true;
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            throw new OnlineInventoryDatabaseException("Thread interrupted while acquiring lock.", e);
        } catch (SQLException e) {
            throw new OnlineInventoryDatabaseException("Error updating online inventory quantity.", e);
        } finally {
            if (acquired) {
                inventoryLock.unlock(); 
            }
        }
    }
    
   public boolean removeOnlineInventoryByProductId(int productId) throws OnlineInventoryDatabaseException {
    boolean acquired = false;
    inventoryLock.lock(); 

    String sql = "DELETE FROM online_inventory WHERE product_id = ?";
    
    try {
        acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS); 
        if (!acquired) {
            throw new OnlineInventoryDatabaseException("Could not acquire lock. Try again later.");
        }

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) { // Get fresh connection
            connection.setAutoCommit(false); 

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, productId);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new OnlineInventoryDatabaseException("No inventory record found for product ID: " + productId);
                }

                connection.commit(); 
                return true;
            }
        }

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); 
        throw new OnlineInventoryDatabaseException("Thread interrupted while acquiring lock.", e);
    } catch (SQLException e) {
        throw new OnlineInventoryDatabaseException("Error deleting online inventory by product ID.", e);
    } finally {
        if (acquired) {
            inventoryLock.unlock(); 
        }
    }
}

    public OnlineInventory getOnlineInventoryById(int onlineInventoryId) throws OnlineInventoryDatabaseException {
        Logger logger = Logger.getLogger(OnlineInventoryDAO.class.getName()); 
        String sql = "SELECT * FROM online_inventory WHERE online_inventory_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, onlineInventoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new OnlineInventory(
                        rs.getInt("online_inventory_id"),
                        rs.getInt("product_id"),
                        rs.getInt("max_capacity"),
                        rs.getInt("current_quantity"),
                        rs.getInt("restock_threshold"),
                        rs.getTimestamp("timestamp")
                    );
                }
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving online inventory by ID: " + onlineInventoryId + " - " + e.getMessage()); 
            throw new OnlineInventoryDatabaseException("Error fetching online inventory by ID.", e);
        }

        logger.warning("No online inventory found for ID: " + onlineInventoryId);
        return null;
    }
    
    public List<OnlineInventory> getAllInventoryItems(int limit, int offset) throws SQLException {
    Logger logger = Logger.getLogger(OnlineInventoryDAO.class.getName()); 
    List<OnlineInventory> inventoryItems = new ArrayList<>();
    String query = "SELECT * FROM online_inventory LIMIT ? OFFSET ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setInt(1, limit);
        statement.setInt(2, offset);
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                inventoryItems.add(new OnlineInventory(
                    resultSet.getInt("online_inventory_id"),
                    resultSet.getInt("product_id"),
                    resultSet.getInt("max_capacity"),
                    resultSet.getInt("current_quantity"),
                    resultSet.getInt("restock_threshold"),
                    resultSet.getTimestamp("timestamp")
                ));
            }
        }

        logger.info("Successfully retrieved " + inventoryItems.size() + " inventory records."); 

    } catch (SQLException e) {
        logger.severe("Error retrieving inventory records: " + e.getMessage()); 
        throw new SQLException("Error retrieving inventory records.", e);
    }
    return inventoryItems;
}

   public boolean reduceStockByProductId(int productId, int quantity, Connection connection) throws SQLException {
    boolean acquired = inventoryLock.tryLock();

    if (!acquired) {
        throw new SQLException("Could not acquire lock. Try again later.");
    }

    String checkQuery = "SELECT current_quantity FROM online_inventory WHERE product_id = ?";
    String updateQuery = "UPDATE online_inventory SET current_quantity = current_quantity - ? WHERE product_id = ?";

    try {
        connection.setAutoCommit(false); 

        int currentQuantity = 0;
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, productId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    currentQuantity = rs.getInt("current_quantity");
                    System.out.println("Before Update: Product " + productId + " has quantity " + currentQuantity);

                    if (currentQuantity < quantity) {
                        System.err.println("Not enough stock for Product ID: " + productId);
                        return false; 
                    }
                } else {
                    return false; // If product not found return false
                }
            }
        }

        
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, quantity);
            updateStmt.setInt(2, productId);
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated == 0) {
                return false;
            }
        }

        connection.commit(); // Commit transaction
        System.out.println("Stock successfully reduced for Product ID: " + productId + " by " + quantity);

        return true;
    } catch (SQLException e) {
        connection.rollback(); // Rollback in case of failure
        System.err.println("Stock reduction failed for Product ID: " + productId);
        throw e;
    } finally {
        inventoryLock.unlock(); // Release lock
    }
}

    public Optional<Integer> getAvailableBatchIdForProduct(int productId, int requiredQuantity) throws SQLException {
        Logger logger = Logger.getLogger(OnlineInventoryDAO.class.getName()); 
        String query = """
            SELECT oi.batch_id
            FROM online_inventory oi
            INNER JOIN batches b ON oi.batch_id = b.batch_id
            WHERE oi.product_id = ? AND oi.current_quantity >= ?
            ORDER BY b.expiry_date ASC
            LIMIT 1
        """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, productId);       
            stmt.setInt(2, requiredQuantity); 

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("batch_id")); 
                }
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving available batch for product ID: " + productId + " - " + e.getMessage()); 
            throw new SQLException("Error retrieving available batch for product ID: " + productId, e);
        }

        logger.info("No batch with sufficient stock found for product ID: " + productId); 
        return Optional.empty(); 
    }

    public int getAvailableBatchId(int productId, int requiredQuantity) throws SQLException {
        Logger logger = Logger.getLogger(OnlineInventoryDAO.class.getName()); 
        String query = """
            SELECT oi_items.batch_id 
            FROM online_inventory_items oi_items 
            JOIN online_inventory oi ON oi.online_inventory_id = oi_items.online_inventory_id 
            WHERE oi.product_id = ? AND oi.current_quantity >= ? 
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
            logger.severe("Error retrieving available batch for product ID: " + productId + " - " + e.getMessage()); 
            throw new SQLException("Error retrieving batch ID for product ID: " + productId, e);
        }

        logger.info("No batch with sufficient stock found for product ID: " + productId);
        return -1; 
    }

    public boolean reduceStockByBatchId(int batchId, int quantity, Connection connection) throws SQLException, InterruptedException {
        boolean acquired = inventoryLock.tryLock(5, TimeUnit.SECONDS);
        if (!acquired) {
            throw new SQLException("Could not acquire lock. Try again later.");
        }

        try {
            // Check if stock is available
            String checkQuery = "SELECT current_quantity FROM online_inventory WHERE batch_id = ?";
            int currentQuantity = 0;

            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, batchId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        currentQuantity = rs.getInt("current_quantity");
                        if (currentQuantity < quantity) {
                            throw new SQLException("Insufficient stock for batch ID: " + batchId);
                        }
                    } else {
                        throw new SQLException("Batch ID " + batchId + " does not exist.");
                    }
                }
            }

            // Reduce stock only in online_inventory table
            String updateQuery = "UPDATE online_inventory SET current_quantity = current_quantity - ? WHERE batch_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, batchId);
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("Stock update failed: No matching batch ID found.");
                }
            }

            return true; // Return success status
        } finally {
            inventoryLock.unlock();
        }
    }

    public int getCurrentQuantityByProductId(int productId) throws SQLException {
        Logger logger = Logger.getLogger(OnlineInventoryDAO.class.getName());
        String query = "SELECT current_quantity FROM online_inventory WHERE product_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, productId); 

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("current_quantity"); 
                }
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving current quantity for product ID: " + productId + " - " + e.getMessage()); 
            throw new SQLException("Error retrieving current quantity for product ID: " + productId, e);
        }

        logger.info("No record found for product ID: " + productId + ". Returning 0."); 
        return 0; 
    }
}
