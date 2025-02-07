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
import com.mycompany.database_syos.models.ProductCategory;
import com.mycompany.database_syos.exceptions.ProductDatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDAO {
    
    public ProductCategoryDAO() {
  
    }

   public List<ProductCategory> getProductCategoriesBySubCategory(int subCategoryId) throws ProductDatabaseException {
    String sql = "SELECT product_category_id, product_category_name, product_category_code FROM product_category WHERE sub_category_id = ?";
    List<ProductCategory> productCategories = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, subCategoryId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("product_category_id");
                String name = rs.getString("product_category_name");
                String code = rs.getString("product_category_code");
                productCategories.add(new ProductCategory(id, name, code));
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch product categories", e);
    }

    return productCategories;
}

    public ProductCategory getProductCategoryById(int productCategoryId) throws ProductDatabaseException {
       String sql = "SELECT product_category_id, product_category_name, product_category_code FROM product_category WHERE product_category_id = ?";

       try (Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

           pstmt.setInt(1, productCategoryId);
           try (ResultSet rs = pstmt.executeQuery()) {
               if (rs.next()) {
                   return new ProductCategory(
                       rs.getInt("product_category_id"),
                       rs.getString("product_category_name"),
                       rs.getString("product_category_code")
                   );
               } else {
                   throw new ProductDatabaseException("Product category not found for ID: " + productCategoryId);
               }
           }
       } catch (SQLException e) {
           throw new ProductDatabaseException("Failed to fetch product category by ID: " + productCategoryId, e);
       }
   }

   public int createProductCategory(String name, String code, int subCategoryId) throws ProductDatabaseException {
    String sql = "INSERT INTO product_category (product_category_name, product_category_code, sub_category_id) VALUES (?, ?, ?)";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

        pstmt.setString(1, name);
        pstmt.setString(2, code);
        pstmt.setInt(3, subCategoryId);
        pstmt.executeUpdate();

        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new ProductDatabaseException("Failed to create a new product category.");
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Error creating new product category", e);
    }
}

}
