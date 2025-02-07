/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.dao;


import com.mycompany.database_syos.databaseconnection.DatabaseConnection;
import com.mycompany.database_syos.exceptions.ShelfDatabaseException;
import com.mycompany.database_syos.models.ShelfItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */

public class ShelfItemDAO {

    public ShelfItemDAO() {     
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
        throw new ShelfDatabaseException("Error adding shelf item to the database.", e);
    }
}

   public List<ShelfItem> getShelfItemsForProduct(int productId) throws ShelfDatabaseException {
    String sql = "SELECT * FROM shelf_items WHERE product_id = ? ORDER BY shelf_item_id ASC";
    List<ShelfItem> shelfItems = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) { 

        pstmt.setInt(1, productId);

        try (ResultSet rs = pstmt.executeQuery()) {  
            while (rs.next()) {
                shelfItems.add(new ShelfItem(
                    rs.getInt("shelf_item_id"), 
                    rs.getInt("shelf_id"),     
                    rs.getInt("batch_id"), 
                    rs.getString("item_serial_number"), 
                    rs.getInt("product_id")
                ));
            }
        }

    } catch (SQLException e) {
        throw new ShelfDatabaseException("Error retrieving shelf items for product ID: " + productId, e);
    }
    return shelfItems;
}

    public void updateShelfItemQuantity(int shelfItemId, int newQuantity) throws ShelfDatabaseException {
    String sql = "UPDATE shelf_items SET quantity = ? WHERE shelf_item_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql)) {  

        pstmt.setInt(1, newQuantity);
        pstmt.setInt(2, shelfItemId);
        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected == 0) {
            throw new ShelfDatabaseException("No shelf item found with ID: " + shelfItemId);
        }

    } catch (SQLException e) {
        throw new ShelfDatabaseException("Error updating shelf item quantity for ID: " + shelfItemId, e);
    }
}

   public void removeShelfItem(int shelfItemId) throws ShelfDatabaseException {
    String sql = "DELETE FROM shelf_items WHERE shelf_item_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql)) {  

        pstmt.setInt(1, shelfItemId);
        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected == 0) {
            throw new ShelfDatabaseException("No shelf item found with ID: " + shelfItemId);
        }

    } catch (SQLException e) {
        throw new ShelfDatabaseException("Error deleting shelf item with ID: " + shelfItemId, e);
    }
}

    public ShelfItem getShelfItemBySerialNumber(String itemSerialNumber) throws SQLException {
       String sql = "SELECT * FROM shelf_items WHERE item_serial_number = ?";

       try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
            PreparedStatement pstmt = connection.prepareStatement(sql)) {  

           pstmt.setString(1, itemSerialNumber);

           try (ResultSet rs = pstmt.executeQuery()) {  
               if (rs.next()) {
                   return new ShelfItem(
                       rs.getInt("shelf_item_id"),
                       rs.getInt("shelf_id"),
                       rs.getInt("batch_id"),
                       rs.getString("item_serial_number"),
                       rs.getInt("product_id")
                   );
               }
           }

       } catch (SQLException e) {
           throw new SQLException("Error retrieving shelf item by serial number: " + itemSerialNumber, e);
       }
       return null;
   }

}
