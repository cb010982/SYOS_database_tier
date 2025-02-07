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
import com.mycompany.database_syos.models.Shelf;
import com.mycompany.database_syos.exceptions.ShelfDatabaseException;
import com.mycompany.database_syos.models.ShelfItem;
import com.mycompany.database_syos.exceptions.ShelfNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;


public class ShelfDAO {
    public ShelfDAO() {
        
    }

    public void addShelf(Shelf shelf) throws ShelfDatabaseException {
    String sql = "INSERT INTO shelf (product_id, max_capacity, current_quantity, last_restock_timestamp) VALUES (?, ?, ?, ?)";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, shelf.getProductId());
        pstmt.setInt(2, shelf.getMaxCapacity());
        pstmt.setInt(3, shelf.getCurrentQuantity());
        pstmt.setTimestamp(4, shelf.getLastRestockTimestamp());
        pstmt.executeUpdate();
    } catch (SQLException e) {
        throw new ShelfDatabaseException("Error adding shelf to the database.", e);
    }
}

    public List<Shelf> getAllShelves() throws ShelfDatabaseException {
      List<Shelf> shelves = new ArrayList<>();
      String sql = "SELECT * FROM shelf";

      try (Connection connection = DatabaseConnection.getInstance().getConnection();
           PreparedStatement pstmt = connection.prepareStatement(sql);
           ResultSet rs = pstmt.executeQuery()) {
          while (rs.next()) {
              Shelf shelf = new Shelf(
                  rs.getInt("shelf_id"),
                  rs.getInt("product_id"),
                  rs.getInt("max_capacity"),
                  rs.getInt("current_quantity"),
                  rs.getTimestamp("last_restock_timestamp")
              );
              shelves.add(shelf);
          }
      } catch (SQLException e) {
          throw new ShelfDatabaseException("Error retrieving shelves from the database.", e);
      }
      return shelves;
  }

   public Shelf findShelfById(int shelfId) throws ShelfDatabaseException, ShelfNotFoundException {
    String sql = "SELECT * FROM shelf WHERE shelf_id = ?";
    
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, shelfId);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Shelf(
                    rs.getInt("shelf_id"),
                    rs.getInt("product_id"),
                    rs.getInt("max_capacity"),
                    rs.getInt("current_quantity"),
                    rs.getTimestamp("last_restock_timestamp")
                );
            } else {
                throw new ShelfNotFoundException("Shelf with ID " + shelfId + " not found.");
            }
        }
    } catch (SQLException e) {
        throw new ShelfDatabaseException("Error finding shelf by ID in the database.", e);
    }
}

    public boolean updateMaxCapacity(Shelf shelf) throws ShelfDatabaseException {
    String sql = "UPDATE shelf SET max_capacity = ? WHERE shelf_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, shelf.getMaxCapacity());
        pstmt.setInt(2, shelf.getShelfId());

        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        throw new ShelfDatabaseException("Error updating shelf max capacity in the database.", e);
    }
}

    public boolean updateShelfQuantity(int shelfId, int newQuantity) throws ShelfDatabaseException {
    String sql = "UPDATE shelf SET current_quantity = ? WHERE shelf_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, newQuantity);
        pstmt.setInt(2, shelfId);

        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        throw new ShelfDatabaseException("Error updating shelf quantity in the database.", e);
    }
}

    public void addShelfItem(ShelfItem shelfItem) throws ShelfDatabaseException {
      String sql = "INSERT INTO shelf_items (shelf_id, batch_id, item_serial_number, product_id) VALUES (?, ?, ?, ?)";

      try (Connection connection = DatabaseConnection.getInstance().getConnection();
           PreparedStatement pstmt = connection.prepareStatement(sql)) {
          pstmt.setInt(1, shelfItem.getShelfId());
          pstmt.setInt(2, shelfItem.getBatchId());
          pstmt.setString(3, shelfItem.getItemSerialNumber());
          pstmt.setInt(4, shelfItem.getProductId());
          pstmt.executeUpdate();
      } catch (SQLException e) {
          throw new ShelfDatabaseException("Error adding item to shelf inventory.", e);
      }
  }

   public boolean deleteShelf(int shelfId) throws ShelfDatabaseException {
    String sql = "DELETE FROM shelf WHERE shelf_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, shelfId);
        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        throw new ShelfDatabaseException("Error deleting shelf from the database.", e);
    }
}

    public void displayAllProducts() {
    String sql = "SELECT product_id, name FROM product"; 

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        System.out.println("Product ID | Product Name");
        System.out.println("------------------------");
        while (rs.next()) {
            int productId = rs.getInt("product_id");
            String productName = rs.getString("name"); 
            System.out.println(productId + " | " + productName);
        }
    } catch (SQLException e) {
        System.err.println("Error retrieving product list: " + e.getMessage());
    }
}

   public ShelfItem getShelfItemBySerialNumber(String itemSerialNumber) throws SQLException {
    String sql = "SELECT * FROM shelf_items WHERE item_serial_number = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, itemSerialNumber);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int shelfItemId = rs.getInt("shelf_item_id");
                int shelfId = rs.getInt("shelf_id");  
                int batchId = rs.getInt("batch_id");
                String serialNumber = rs.getString("item_serial_number");
                int productId = rs.getInt("product_id");

                System.out.println("shelf_item_id: " + shelfItemId);
                System.out.println("shelf_id: " + shelfId);  
                System.out.println("batch_id: " + batchId);
                System.out.println("item_serial_number: " + serialNumber);
                System.out.println("product_id: " + productId);

                return new ShelfItem(shelfItemId, shelfId, batchId, serialNumber, productId);
            } else {
                System.out.println("No shelf item found with serial number: " + itemSerialNumber);
                return null; 
            }
        }
    } catch (SQLException e) {
        System.err.println("Error retrieving shelf item by serial number: " + e.getMessage());
        throw e;
    }
}


}
