/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.dao;


import com.mycompany.database_syos.databaseconnection.DatabaseConnection;
import com.mycompany.database_syos.models.Cart;
import com.mycompany.database_syos.models.CartItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author User
 */
public class CartDAO {
    public void someDatabaseOperation() {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM cart");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("Cart Item: " + rs.getString("item_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProductToCart(int productId, int quantity) {
        String sql = "INSERT INTO cart_items (product_id, quantity) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false); 

            pstmt.setInt(1, productId);
            pstmt.setInt(2, quantity);
            pstmt.executeUpdate();

            connection.commit(); 
            System.out.println("Product added to cart.");

        } catch (SQLException e) {
            System.out.println("Error adding product to cart.");
            e.printStackTrace();
        }
    }

    public int getCartIdByUserId(int userId) {
        String sql = "SELECT cart_id FROM carts WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";  

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cart_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }
    
    private final ReentrantLock cartLock = new ReentrantLock();

    public void addItemToCart(int cartId, int productId, int batchId, String itemSerialNumber, int quantity, double price) {
        cartLock.lock(); // Acquire the lock
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) { // Get connection from HikariCP
            // Validate the cart existence
            if (!cartExists(cartId)) {
                System.out.println("Cart with ID " + cartId + " does not exist. Please create a cart first.");
                return;
            }

            // Validate the batch ID
            if (!isBatchIdValid(batchId)) {
                System.out.println("Batch ID " + batchId + " does not exist in batches table.");
                return;
            }

            // Prepare and execute the insert statements
            String sql = "INSERT INTO cart_items (cart_id, product_id, batch_id, item_serial_number, quantity, price) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, cartId);              
                pstmt.setInt(2, productId);           
                pstmt.setInt(3, batchId);            
                pstmt.setString(4, itemSerialNumber); 
                pstmt.setInt(5, quantity);            
                pstmt.setDouble(6, price);           
                pstmt.executeUpdate();

                System.out.println("Product with ID " + productId + " added to cart " + cartId + " with batch ID " + batchId);
            }

        } catch (SQLException e) {
            System.out.println("Error adding product to cart.");
            e.printStackTrace();
        } finally {
            cartLock.unlock(); // Release the lock
        }
    }

    public boolean isBatchIdValid(int batchId) {
        String query = "SELECT COUNT(*) FROM batches WHERE batch_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, batchId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error validating batch ID: " + batchId);
            e.printStackTrace();
        }
        return false; 
    }

    public Cart getCartById(int cartId) {
        String sql = "SELECT * FROM carts WHERE cart_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, cartId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Cart(
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("cart_no"),
                        rs.getDate("date"),
                        rs.getDouble("total"),
                        rs.getDouble("gross_total"),
                        rs.getDouble("discount"),
                        rs.getDouble("net_total"),
                        rs.getTimestamp("created_at"),
                        rs.getString("telephone"),
                        rs.getString("address")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving cart with ID: " + cartId);
            e.printStackTrace();
        }
        return null;
    }
    
    public List<CartItem> getCartItemsByCartId(int cartId) {
      String sql = """
          SELECT ci.cart_item_id, ci.cart_id, ci.product_id, ci.batch_id, ci.item_serial_number,
                 ci.quantity, ci.price, ci.created_at, p.name AS product_name
          FROM cart_items ci
          JOIN product p ON ci.product_id = p.product_id
          WHERE ci.cart_id = ?
      """;

      List<CartItem> cartItems = new ArrayList<>();

      try (Connection connection = DatabaseConnection.getInstance().getConnection();
           PreparedStatement pstmt = connection.prepareStatement(sql)) {

          pstmt.setInt(1, cartId);

          try (ResultSet rs = pstmt.executeQuery()) {
              while (rs.next()) {
                  CartItem item = new CartItem(
                      rs.getInt("cart_item_id"),
                      rs.getInt("cart_id"),
                      rs.getInt("product_id"),
                      rs.getInt("batch_id"),
                      rs.getString("item_serial_number"),
                      rs.getInt("quantity"),
                      rs.getTimestamp("created_at").toLocalDateTime(),
                      rs.getDouble("price")
                  );
                  item.setProductName(rs.getString("product_name"));
                  cartItems.add(item);
              }
          }
      } catch (SQLException e) {
          System.out.println("Error retrieving cart items for cart ID: " + cartId);
          e.printStackTrace();
      }

      return cartItems;
  }

    private boolean cartExists(int cartId) {
       String sql = "SELECT COUNT(*) FROM carts WHERE cart_id = ?";

       try (Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

           pstmt.setInt(1, cartId);

           try (ResultSet rs = pstmt.executeQuery()) {
               if (rs.next()) {
                   return rs.getInt(1) > 0; 
               }
           }
       } catch (SQLException e) {
           System.out.println("Error checking if cart exists for ID: " + cartId);
           e.printStackTrace();
       }
       return false; 
   }

    public int saveCart(Cart cart) {
        String sql = "INSERT INTO carts (user_id, cart_no, date, total, gross_total, discount, net_total, created_at, telephone, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = null;  
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false); 

            try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, cart.getUserId());
                pstmt.setString(2, cart.getCartNo());
                pstmt.setTimestamp(3, new Timestamp(cart.getDate().getTime()));
                pstmt.setDouble(4, cart.getTotal());
                pstmt.setDouble(5, cart.getGrossTotal());
                pstmt.setDouble(6, cart.getDiscount());
                pstmt.setDouble(7, cart.getNetTotal());
                pstmt.setTimestamp(8, cart.getCreatedAt());
                pstmt.setString(9, cart.getTelephone());
                pstmt.setString(10, cart.getAddress());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating cart failed, no rows affected.");
                }

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedCartId = rs.getInt(1);
                        cart.setCartId(generatedCartId);
                        connection.commit(); 
                        return generatedCartId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving cart: " + e.getMessage());
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // Making sure the connection is closed.
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
        return -1;
    }

   public boolean updateCartContactInfo(int cartId, String telephone, String address) {
    String sql = "UPDATE carts SET telephone = ?, address = ? WHERE cart_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setString(1, telephone);
        pstmt.setString(2, address);
        pstmt.setInt(3, cartId);

        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0; 

    } catch (SQLException e) {
        System.err.println("Error updating cart contact info for Cart ID " + cartId + ": " + e.getMessage());
        e.printStackTrace();
    }
    return false;  
}

    public void updateOnlineInventoryQuantity(int onlineInventoryId, int newQuantity) {
        String sql = "UPDATE online_inventory SET current_quantity = ? WHERE online_inventory_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, onlineInventoryId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Online Inventory with ID " + onlineInventoryId + " updated to new quantity: " + newQuantity);
            } else {
                System.out.println("No inventory item found with ID: " + onlineInventoryId);
            }

        } catch (SQLException e) {
            System.err.println("Error updating online inventory for ID " + onlineInventoryId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateCartTotals(int cartId, double total, double grossTotal, double netTotal) {
      String sql = "UPDATE carts SET total = ?, gross_total = ?, net_total = ? WHERE cart_id = ?";

      try (Connection connection = DatabaseConnection.getInstance().getConnection();
           PreparedStatement pstmt = connection.prepareStatement(sql)) {

          pstmt.setDouble(1, total);
          pstmt.setDouble(2, grossTotal);
          pstmt.setDouble(3, netTotal);
          pstmt.setInt(4, cartId);

          int affectedRows = pstmt.executeUpdate();

          if (affectedRows > 0) {
              System.out.println("Cart totals updated successfully for Cart ID: " + cartId);
          } else {
              System.out.println("No cart found with ID: " + cartId);
          }

      } catch (SQLException e) {
          System.err.println("Error updating cart totals for Cart ID " + cartId + ": " + e.getMessage());
          e.printStackTrace();
      }
  }

    public void addProductToCart(int cartId, int productId, int quantity) {
        String query = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE quantity = quantity + ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, cartId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, quantity);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Product " + productId + " added/updated in cart " + cartId + " with quantity " + quantity);
            } else {
                System.out.println("Failed to add/update product in cart.");
            }

        } catch (SQLException e) {
            System.err.println("Error adding product to cart for Cart ID " + cartId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    public void removeProductFromCart(int cartId, int productId) {
       String sql = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";

       try (Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

           pstmt.setInt(1, cartId);
           pstmt.setInt(2, productId);

           int affectedRows = pstmt.executeUpdate();

           if (affectedRows > 0) {
               System.out.println("Product " + productId + " removed from cart " + cartId);
           } else {
               System.out.println("Product " + productId + " not found in cart " + cartId);
           }

       } catch (SQLException e) {
           System.err.println("Error removing product from cart for Cart ID " + cartId + ": " + e.getMessage());
           e.printStackTrace();
       }
   }
    
    public void saveCartAsOrder(int userId, Cart cart) {
       String query = "INSERT INTO orders (user_id, total_amount, created_at) VALUES (?, ?, NOW())";
       Connection connection = null;

       try {
           connection = DatabaseConnection.getInstance().getConnection();
           connection.setAutoCommit(false); // Start transaction

           try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
               stmt.setInt(1, userId);
               stmt.setDouble(2, cart.getTotalAmount());
               stmt.executeUpdate();

               try (ResultSet rs = stmt.getGeneratedKeys()) {
                   if (rs.next()) {
                       int orderId = rs.getInt(1);
                       saveOrderItems(connection, orderId, cart.getItems()); // Save order items
                       connection.commit(); // Commit transaction
                       System.out.println("Order saved successfully with ID: " + orderId);
                   }
               }
           }

       } catch (SQLException e) {
           System.err.println("Error saving order for user ID " + userId + ": " + e.getMessage());
           e.printStackTrace();
           if (connection != null) {
               try {
                   connection.rollback();
                   System.out.println("Transaction rolled back.");
               } catch (SQLException rollbackEx) {
                   rollbackEx.printStackTrace();
               }
           }
       } finally {
           if (connection != null) {
               try {
                   connection.close(); 
               } catch (SQLException closeEx) {
                   closeEx.printStackTrace();
               }
           }
       }
   }

    private void saveOrderItems(Connection connection, int orderId, List<CartItem> items) throws SQLException {
        String query = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (CartItem item : items) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getPrice());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    public void clearCart(int cartId) {
        String deleteCartItemsQuery = "DELETE FROM cart_items WHERE cart_id = ?";
        String deleteCartQuery = "DELETE FROM cart WHERE cart_id = ?";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt1 = connection.prepareStatement(deleteCartItemsQuery)) {
                stmt1.setInt(1, cartId);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = connection.prepareStatement(deleteCartQuery)) {
                stmt2.setInt(1, cartId);
                stmt2.executeUpdate();
            }

            connection.commit(); // Commit transaction
            System.out.println("Cart with ID " + cartId + " cleared successfully.");

        } catch (SQLException e) {
            System.err.println("Error clearing cart with ID " + cartId + ": " + e.getMessage());
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback if error occurs
                    System.out.println("Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // Ensure connection is closed
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    public void removeItemsFromCart(int cartId, int productId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = quantity - ? WHERE cart_id = ? AND product_id = ?";

        cartLock.lock(); // Acquire the lock
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, cartId);
                pstmt.setInt(3, productId);
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Decremented quantity of product ID " + productId + " in cart.");
                } else {
                    System.out.println("No items updated for cartId=" + cartId + ", productId=" + productId);
                }

                connection.commit(); // Commit the transaction
            }

        } catch (SQLException e) {
            System.out.println("Error updating cart items. Rolling back transaction.");
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback the transaction
                    System.out.println("Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); 
                    connection.close(); // Ensure connection is closed
                } catch (SQLException e) {
                    System.out.println("Error resetting auto-commit or closing connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            cartLock.unlock(); // Release the lock
        }
    }
    
    public void updateCartItemQuantity(int cartId, int productId, int newQuantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, cartId);
            pstmt.setInt(3, productId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Updated cart item: cartId=" + cartId + ", productId=" + productId + ", newQuantity=" + newQuantity);
            } else {
                System.out.println("No item found to update for cartId=" + cartId + ", productId=" + productId);
            }

        } catch (SQLException e) {
            System.err.println("Error updating cart item for cartId=" + cartId + ", productId=" + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getBatchIdForProduct(int productId) throws SQLException {
        String query = "SELECT batch_id FROM batches WHERE product_id = ? ORDER BY expiry_date ASC LIMIT 1";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("batch_id");
                } else {
                    throw new SQLException("No batch found for product ID: " + productId);
                }
            }
        }
    }

    public void addItemToCart(int cartId, int productId, int quantity, String itemSerialNumber, double price, OnlineInventoryDAO onlineInventoryDAO) throws SQLException, InterruptedException {
        int batchId = onlineInventoryDAO.getAvailableBatchId(productId, quantity); 
        if (batchId == -1) {
            System.out.println("No batch with sufficient stock for product ID: " + productId);
            return; 
        }

        String query = "INSERT INTO cart_items (cart_id, product_id, batch_id, item_serial_number, quantity, price) VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Insert item into cart
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, cartId);            
                pstmt.setInt(2, productId);         
                pstmt.setInt(3, batchId);            
                pstmt.setString(4, itemSerialNumber); 
                pstmt.setInt(5, quantity);           
                pstmt.setDouble(6, price);           
                pstmt.executeUpdate();
            }

       
            boolean stockUpdated = onlineInventoryDAO.reduceStockByBatchId(batchId, quantity, connection);
            if (!stockUpdated) {
                throw new SQLException("Stock update failed for batch ID: " + batchId);
            }

            connection.commit();
            System.out.println("Added " + quantity + " items of product ID " + productId + " with batch ID " + batchId + " to cart " + cartId);

        } catch (SQLException e) {
            System.out.println("Error adding item to cart. Rolling back transaction.");
            if (connection != null) {
                try {
                    connection.rollback(); 
                    System.out.println("Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); 
                    connection.close(); // Ensure connection is closed
                } catch (SQLException closeEx) {
                    System.out.println("Error resetting auto-commit or closing connection: " + closeEx.getMessage());
                }
            }
        }
    }
    
//    public void addItemToCart(int cartId, int productId, int quantity, String itemSerialNumber, double price, OnlineInventoryDAO onlineInventoryDAO) throws SQLException, InterruptedException {
//    // Removing batchId fetch and check since we are removing batch handling
//    String query = "INSERT INTO cart_items (cart_id, product_id, item_serial_number, quantity, price) VALUES (?, ?, ?, ?, ?)";
//    Connection connection = null;
//
//    try {
//        connection = DatabaseConnection.getInstance().getConnection();
//        connection.setAutoCommit(false); // Start transaction
//
//        // Insert item into cart
//        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//            pstmt.setInt(1, cartId);            
//            pstmt.setInt(2, productId);         
//            pstmt.setString(3, itemSerialNumber); 
//            pstmt.setInt(4, quantity);           
//            pstmt.setDouble(5, price);           
//            pstmt.executeUpdate();
//        }
//
//        // Reduce stock in online inventory by product ID
//        boolean stockUpdated = onlineInventoryDAO.reduceStockByProductId(productId, quantity, connection);
//        if (!stockUpdated) {
//            throw new SQLException("Stock update failed for product ID: " + productId);
//        }
//
//        connection.commit();
//        System.out.println("Added " + quantity + " items of product ID " + productId + " to cart " + cartId);
//
//    } catch (SQLException e) {
//        System.out.println("Error adding item to cart. Rolling back transaction.");
//        if (connection != null) {
//            try {
//                connection.rollback(); 
//                System.out.println("Transaction rolled back.");
//            } catch (SQLException rollbackEx) {
//                rollbackEx.printStackTrace();
//            }
//        }
//        throw e;
//
//    } finally {
//        if (connection != null) {
//            try {
//                connection.setAutoCommit(true); 
//                connection.close(); // Ensure connection is closed
//            } catch (SQLException closeEx) {
//                System.out.println("Error resetting auto-commit or closing connection: " + closeEx.getMessage());
//            }
//        }
//    }
//}


    public int getAvailableBatchId(int productId, int requiredQuantity) {
        String query = """
            SELECT batch_id
            FROM online_inventory
            WHERE product_id = ? AND current_quantity >= ?
            ORDER BY timestamp ASC
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
            System.err.println("Error fetching available batch for product ID " + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return -1; 
    }

    public List<CartItem> getCartItemsByUserId(int userId) {
        String sql = """
            SELECT ci.cart_item_id, ci.cart_id, ci.product_id, ci.batch_id, ci.item_serial_number,
                   ci.quantity, ci.price, ci.created_at, p.name AS product_name
            FROM cart_items ci
            JOIN carts c ON ci.cart_id = c.cart_id
            JOIN product p ON ci.product_id = p.product_id
            WHERE c.user_id = ?
        """;

        List<CartItem> cartItems = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem(
                        rs.getInt("cart_item_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("product_id"),
                        rs.getInt("batch_id"),
                        rs.getString("item_serial_number"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getDouble("price")
                    );
                    item.setProductName(rs.getString("product_name"));
                    cartItems.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving cart items for user ID: " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return cartItems;
    }
}
