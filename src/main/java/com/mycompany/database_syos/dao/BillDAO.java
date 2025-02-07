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
import com.mycompany.database_syos.models.Bill;
import com.mycompany.database_syos.models.BillItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public BillDAO() {
        
    }

    public void saveBill(Bill bill) {
        String sql = "INSERT INTO bills (user_id, bill_no, date, gross_total, total, discount, loyalty_deduction, net_total, cash_received, change_given, payment_type, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection(); // Fetch from HikariCP
             PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {  

            preparedStatement.setInt(1, bill.getUserId());
            preparedStatement.setString(2, bill.getBillNo());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(bill.getDate()));
            preparedStatement.setDouble(4, bill.getGrossTotal());
            preparedStatement.setDouble(5, bill.getTotal());
            preparedStatement.setDouble(6, bill.getDiscount());
            preparedStatement.setDouble(7, bill.getLoyaltyDeduction());
            preparedStatement.setDouble(8, bill.getNetTotal());
            preparedStatement.setDouble(9, bill.getCashReceived());
            preparedStatement.setDouble(10, bill.getChangeGiven());
            preparedStatement.setString(11, bill.getPaymentType());
            preparedStatement.setTimestamp(12, Timestamp.valueOf(bill.getCreatedAt()));
            preparedStatement.setTimestamp(13, Timestamp.valueOf(bill.getUpdatedAt()));

            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {  
                if (rs.next()) {
                    bill.setBillId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saving bill: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    public int getLastBillNumber() {
    String sql = "SELECT bill_no FROM bills ORDER BY bill_id DESC LIMIT 1";
    
    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {  // Used try-with-resources

        if (rs.next()) {
            String billNo = rs.getString("bill_no");
            if (billNo.startsWith("BILL_")) {
                return Integer.parseInt(billNo.substring(5)); 
            }
        }

    } catch (SQLException e) {
        System.err.println("Error retrieving last bill number: " + e.getMessage());
    }
    return 0; 
}

    
    public void saveBillItem(BillItem billItem) {
    String sql = "INSERT INTO bill_items (bill_id, product_id, batch_id, item_serial_number, quantity, price, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) { 

        preparedStatement.setInt(1, billItem.getBillId());
        preparedStatement.setInt(2, billItem.getProductId());
        preparedStatement.setInt(3, billItem.getBatchId());
        preparedStatement.setString(4, billItem.getItemSerialNumber());
        preparedStatement.setInt(5, billItem.getQuantity());
        preparedStatement.setDouble(6, billItem.getPrice());
        preparedStatement.setTimestamp(7, Timestamp.valueOf(billItem.getCreatedAt()));

        preparedStatement.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error saving bill item: " + e.getMessage());
    }
}

    public List<Bill> getAllBills() {
    List<Bill> bills = new ArrayList<>();
    String sql = "SELECT * FROM bills";
    
    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet rs = preparedStatement.executeQuery()) { 

        while (rs.next()) {
            Bill bill = new Bill(
                rs.getInt("user_id"),                            
                rs.getString("bill_no"),                        
                rs.getTimestamp("date").toLocalDateTime(),       
                rs.getDouble("gross_total"),                     
                rs.getDouble("total"),                           
                rs.getDouble("discount"),                        
                rs.getDouble("loyalty_deduction"),             
                rs.getDouble("net_total"),                       
                rs.getDouble("cash_received"),                   
                rs.getDouble("change_given"),                    
                rs.getString("payment_type")                     
            );

            bill.setBillId(rs.getInt("bill_id"));
            bill.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            bill.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

            bills.add(bill);
        }
    } catch (SQLException e) {
        System.err.println("Error retrieving bills: " + e.getMessage());
    }
    return bills;
}

    public boolean deleteBillItem(int billItemId) {
    String sql = "DELETE FROM bill_items WHERE bill_item_id = ?";
    
    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql)) {  //Used try-with-resources

        pstmt.setInt(1, billItemId);
        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0; 

    } catch (SQLException e) {
        System.err.println("Error deleting bill item: " + e.getMessage());
    }
    return false;
}

    public BillItem getBillItemById(int billItemId) throws SQLException {
    String sql = "SELECT * FROM bill_items WHERE bill_item_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(sql)) {  

        pstmt.setInt(1, billItemId);

        try (ResultSet rs = pstmt.executeQuery()) {  
            if (rs.next()) {
                BillItem billItem = new BillItem(
                    rs.getInt("bill_id"),
                    rs.getInt("product_id"),
                    rs.getInt("batch_id"),
                    rs.getString("item_serial_number"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                );

                billItem.setBillItemId(rs.getInt("bill_item_id"));
                billItem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                return billItem;
            }
        }
    } catch (SQLException e) {
        System.err.println("Error retrieving bill item by ID: " + e.getMessage());
        throw e;
    }
    return null;
}

}