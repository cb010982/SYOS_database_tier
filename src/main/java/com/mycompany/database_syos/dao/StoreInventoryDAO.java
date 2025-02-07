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
import com.mycompany.database_syos.exceptions.InventoryException;
import com.mycompany.database_syos.models.Product;
import com.mycompany.database_syos.models.StoreInventory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoreInventoryDAO {

    public StoreInventoryDAO() {
    }

    public void addInventory(StoreInventory inventory) throws InventoryException {
      String sql = "INSERT INTO store_inventory (batch_id, product_id, quantity, expiry_date, is_expirable, item_serial_number) " +
                   "VALUES (?, ?, ?, ?, ?, ?)";

      try (Connection connection = DatabaseConnection.getInstance().getConnection();  
           PreparedStatement pstmt = connection.prepareStatement(sql)) {  

          pstmt.setInt(1, inventory.getBatchId());
          pstmt.setInt(2, inventory.getProductId());
          pstmt.setInt(3, inventory.getQuantity());

          if (inventory.getExpiryDate() != null) {
              pstmt.setDate(4, java.sql.Date.valueOf(inventory.getExpiryDate()));
          } else {
              pstmt.setNull(4, java.sql.Types.DATE);
          }

          pstmt.setBoolean(5, inventory.isExpirable());
          pstmt.setString(6, inventory.getItemSerialNumber());

          pstmt.executeUpdate();  

      } catch (SQLException e) {
          throw new InventoryException("Failed to add inventory", e);
      }
  }

    public Product getProductByCode(String productCode) throws InventoryException {
    Product product = null;
    String sql = "SELECT * FROM product WHERE product_code = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql)) {  

        pstmt.setString(1, productCode);
        try (ResultSet rs = pstmt.executeQuery()) {  

            if (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setProductCategoryId(rs.getInt("product_category_id"));
                product.setBrandId(rs.getInt("brand_id"));
                product.setUnitId(rs.getInt("unit_id"));
                product.setCostPrice(rs.getDouble("cost_price"));
                product.setProfitMargin(rs.getDouble("profit_margin"));
                product.setTaxRate(rs.getDouble("tax_rate"));
                product.setFinalPrice(rs.getDouble("final_price"));
                product.setProductCode(rs.getString("product_code"));
                product.setExpirable(rs.getBoolean("is_expirable"));
                product.setCreatedBy(rs.getString("created_by"));
                product.setMainCategoryId(rs.getInt("main_category_id"));
                product.setSubcategoryId(rs.getInt("sub_category_id"));
            }
        }

    } catch (SQLException e) {
        throw new InventoryException("Failed to retrieve product by code", e);
    }
    return product;
}
    
    public List<StoreInventory> getOldestItemsByDateReceived(int productId) throws InventoryException {
        List<StoreInventory> inventoryList = new ArrayList<>();
        String sql = "SELECT si.inventory_id, si.batch_id, si.product_id, si.quantity, si.expiry_date, si.is_expirable, si.item_serial_number " +
                     "FROM store_inventory si " +
                     "JOIN batches b ON si.batch_id = b.batch_id " +
                     "WHERE si.product_id = ? " +
                     "ORDER BY b.date_received ASC";

        try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {  

            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {  
                while (rs.next()) {
                    StoreInventory inventory = new StoreInventory(
                        rs.getInt("inventory_id"),
                        rs.getInt("batch_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                        rs.getBoolean("is_expirable"),
                        rs.getString("item_serial_number")
                    );
                    inventoryList.add(inventory);
                }
            }
        } catch (SQLException e) {
            throw new InventoryException("Error retrieving items by date received.", e);
        }
        return inventoryList;
    }

    public void updateInventoryQuantity(int inventoryId, int newQuantity) throws InventoryException {
    String sql = "UPDATE store_inventory SET quantity = ? WHERE inventory_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, newQuantity);
        pstmt.setInt(2, inventoryId);
        int rowsUpdated = pstmt.executeUpdate();

        if (rowsUpdated == 0) {
            throw new InventoryException("Inventory item with ID " + inventoryId + " not found.");
        }

    } catch (SQLException e) {
        throw new InventoryException("Failed to update inventory quantity.", e);
    }
}
     
   public List<StoreInventory> getInventoryByProductId(int productId) throws InventoryException {
    List<StoreInventory> inventoryList = new ArrayList<>();
    String sql = "SELECT * FROM store_inventory WHERE product_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql)) {  

        pstmt.setInt(1, productId);
        try (ResultSet rs = pstmt.executeQuery()) {  
            while (rs.next()) {
                StoreInventory inventory = new StoreInventory(
                    rs.getInt("inventory_id"),
                    rs.getInt("batch_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                    rs.getBoolean("is_expirable"),
                    rs.getString("item_serial_number")
                );
                inventoryList.add(inventory);
            }
        }

    } catch (SQLException e) {
        throw new InventoryException("Failed to retrieve inventory", e);
    }
    return inventoryList;
}

   public void deleteInventory(int inventoryId) throws InventoryException {
    String sql = "DELETE FROM store_inventory WHERE inventory_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql)) {  

        pstmt.setInt(1, inventoryId);
        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected == 0) {
            throw new InventoryException("No inventory item found with ID: " + inventoryId);
        }

    } catch (SQLException e) {
        throw new InventoryException("Failed to delete inventory", e);
    }
}
    
    public List<StoreInventory> getExpiringItemsByProductId(int productId) throws InventoryException {
      List<StoreInventory> inventoryList = new ArrayList<>();
      String sql = "SELECT inventory_id, batch_id, product_id, quantity, expiry_date, is_expirable, item_serial_number " +
                   "FROM store_inventory WHERE product_id = ? AND expiry_date IS NOT NULL ORDER BY expiry_date ASC";

      try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
           PreparedStatement pstmt = connection.prepareStatement(sql)) { 

          pstmt.setInt(1, productId);
          try (ResultSet rs = pstmt.executeQuery()) {
              while (rs.next()) {
                  StoreInventory inventory = new StoreInventory(
                      rs.getInt("inventory_id"),
                      rs.getInt("batch_id"),
                      rs.getInt("product_id"),
                      rs.getInt("quantity"),
                      rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                      rs.getBoolean("is_expirable"),
                      rs.getString("item_serial_number")
                  );
                  inventoryList.add(inventory);
              }
          }

      } catch (SQLException e) {
          throw new InventoryException("Error retrieving items by expiry date.", e);
      }
      return inventoryList;
  }

    public StoreInventory getInventoryById(int inventoryId) throws InventoryException {
        String sql = "SELECT * FROM store_inventory WHERE inventory_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) { 

            pstmt.setInt(1, inventoryId);
            try (ResultSet rs = pstmt.executeQuery()) { 
                if (rs.next()) {
                    return new StoreInventory(
                        rs.getInt("inventory_id"),
                        rs.getInt("batch_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                        rs.getBoolean("is_expirable"),
                        rs.getString("item_serial_number")
                    );
                }
            }
        } catch (SQLException e) {
            throw new InventoryException("Failed to retrieve inventory by ID", e);
        }
        return null; 
    }

    public List<StoreInventory> getInventoryByBatchId(int batchId) throws InventoryException {
       List<StoreInventory> inventoryList = new ArrayList<>();
       String sql = "SELECT * FROM store_inventory WHERE batch_id = ?";

       try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
            PreparedStatement pstmt = connection.prepareStatement(sql)) {  

           pstmt.setInt(1, batchId);
           try (ResultSet rs = pstmt.executeQuery()) {  
               while (rs.next()) {
                   StoreInventory inventory = new StoreInventory(
                       rs.getInt("inventory_id"),
                       rs.getInt("batch_id"),
                       rs.getInt("product_id"),
                       rs.getInt("quantity"),
                       rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                       rs.getBoolean("is_expirable"),
                       rs.getString("item_serial_number")
                   );
                   inventoryList.add(inventory);
               }
           }

       } catch (SQLException e) {
           throw new InventoryException("Failed to retrieve inventory by batch ID", e);
       }
       return inventoryList;
   }
    
   public List<StoreInventory> getOldestItemsByProductId(int productId) throws InventoryException {
    List<StoreInventory> inventoryList = new ArrayList<>();
    String sql = "SELECT * FROM store_inventory WHERE product_id = ? ORDER BY date_received ASC";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql)) {  

        pstmt.setInt(1, productId);
        try (ResultSet rs = pstmt.executeQuery()) {  
            while (rs.next()) {
                StoreInventory inventory = new StoreInventory(
                    rs.getInt("inventory_id"),
                    rs.getInt("batch_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                    rs.getBoolean("is_expirable"),
                    rs.getString("item_serial_number")
                );
                inventoryList.add(inventory);
            }
        }

    } catch (SQLException e) {
        throw new InventoryException("Error retrieving oldest items by product ID", e);
    }
    return inventoryList;
}
   
    public List<StoreInventory> getItemsSortedByExpiry(int productId) throws InventoryException {
      List<StoreInventory> inventoryList = new ArrayList<>();
      String sql = "SELECT inventory_id, batch_id, product_id, quantity, expiry_date, is_expirable, item_serial_number FROM store_inventory WHERE product_id = ? ORDER BY expiry_date ASC";

      try (Connection connection = DatabaseConnection.getInstance().getConnection();
           PreparedStatement pstmt = connection.prepareStatement(sql)) {  

          pstmt.setInt(1, productId);
          try (ResultSet rs = pstmt.executeQuery()) { 
              while (rs.next()) {
                  StoreInventory inventory = new StoreInventory(
                      rs.getInt("inventory_id"),
                      rs.getInt("batch_id"),
                      rs.getInt("product_id"),
                      rs.getInt("quantity"),
                      rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                      rs.getBoolean("is_expirable"),
                      rs.getString("item_serial_number")
                  );
                  inventoryList.add(inventory);
              }
          }

      } catch (SQLException e) {
          throw new InventoryException("Error retrieving items by expiry date.", e);
      }
      return inventoryList;
  }

    public List<StoreInventory> getItemsSortedByOldest(int productId) throws InventoryException {
      List<StoreInventory> items = new ArrayList<>();
      String query = "SELECT si.* FROM store_inventory si " +
                     "JOIN batches b ON si.batch_id = b.batch_id " +
                     "WHERE si.product_id = ? " +
                     "ORDER BY b.date_received ASC";

      try (Connection connection = DatabaseConnection.getInstance().getConnection();
           PreparedStatement pstmt = connection.prepareStatement(query)) {  

          pstmt.setInt(1, productId);
          try (ResultSet rs = pstmt.executeQuery()) {  
              while (rs.next()) {
                  StoreInventory item = new StoreInventory(
                      rs.getInt("inventory_id"),
                      rs.getInt("batch_id"),
                      rs.getInt("product_id"),
                      rs.getInt("quantity"),
                      rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                      rs.getBoolean("is_expirable"),
                      rs.getString("item_serial_number")
                  );
                  items.add(item);
              }
          }

      } catch (SQLException e) {
          throw new InventoryException("Error retrieving oldest items by product ID.", e);
      }
      return items;
  }

    public void deleteInventoryById(int inventoryId) throws InventoryException {
        String sql = "DELETE FROM store_inventory WHERE inventory_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(sql)) {  

            pstmt.setInt(1, inventoryId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new InventoryException("No rows deleted; check inventory ID: " + inventoryId);
            }
            System.out.println("Deleted inventory ID: " + inventoryId);

        } catch (SQLException e) {
            throw new InventoryException("Error deleting inventory with ID: " + inventoryId, e);
        }
    }
}