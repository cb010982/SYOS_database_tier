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
import com.mycompany.database_syos.models.Brand;
import com.mycompany.database_syos.models.MainCategory;
import com.mycompany.database_syos.models.Product;
import com.mycompany.database_syos.models.ProductCategory;
import com.mycompany.database_syos.exceptions.ProductDatabaseException;
import com.mycompany.database_syos.exceptions.ProductException;
import com.mycompany.database_syos.models.Subcategory;
import com.mycompany.database_syos.models.Unit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO {
 
    public ProductDAO() {
    }

   public void saveProduct(Product product, int mainCategoryId, int productCategoryId, int brandId, int unitId) throws ProductDatabaseException {
    String sql = "INSERT INTO product (name, main_category_id, sub_category_id, product_category_id, brand_id, unit_id, cost_price, profit_margin, tax_rate, final_price, product_code, is_expirable, created_by) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setString(1, product.getName());
        pstmt.setInt(2, mainCategoryId);
        pstmt.setInt(3, product.getSubcategoryId());
        pstmt.setInt(4, product.getProductCategoryId());
        pstmt.setInt(5, brandId);
        pstmt.setInt(6, unitId);
        pstmt.setDouble(7, product.getCostPrice());
        pstmt.setDouble(8, product.getProfitMargin());
        pstmt.setDouble(9, product.getTaxRate());
        pstmt.setDouble(10, product.getFinalPrice());
        pstmt.setString(11, product.getProductCode());
        pstmt.setBoolean(12, product.isExpirable());
        pstmt.setString(13, product.getCreatedBy());

        pstmt.executeUpdate();
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to save product to the database", e);
    }
}

   public List<Brand> getBrands() throws ProductDatabaseException {
    String sql = "SELECT brand_id, brand_name, brand_code FROM brand";
    List<Brand> brands = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("brand_id");
            String name = rs.getString("brand_name");
            String code = rs.getString("brand_code"); 
            brands.add(new Brand(id, name, code)); 
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch brands from the database", e);
    }

    return brands;
}

   public List<Unit> getUnits() throws ProductDatabaseException {
    String sql = "SELECT unit_id, unit_name FROM units";
    List<Unit> units = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("unit_id");
            String name = rs.getString("unit_name");
            units.add(new Unit(id, name));
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch units from database", e);
    }

    return units;
}

    public List<Subcategory> getCategoriesByMainCategory(int mainCategoryId) throws ProductDatabaseException {
    String sql = "SELECT sub_category_id, sub_category_name, sub_category_code FROM sub_category WHERE main_category_id = ?";
    List<Subcategory> categories = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, mainCategoryId);  
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("sub_category_id");
                String name = rs.getString("sub_category_name");
                String code = rs.getString("sub_category_code");
                categories.add(new Subcategory(id, name, code));
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch categories for the main category ID: " + mainCategoryId, e);
    }

    return categories;
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
            throw new ProductDatabaseException("Failed to fetch product categories for the subcategory ID: " + subCategoryId, e);
        }

        return productCategories;
    }

    public List<MainCategory> getMainCategories() throws ProductDatabaseException {
    String sql = "SELECT main_category_id, main_category_name, main_category_code FROM main_category";
    List<MainCategory> mainCategories = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("main_category_id");
            String name = rs.getString("main_category_name");
            String code = rs.getString("main_category_code");
            mainCategories.add(new MainCategory(id, name, code)); 
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch main categories from the database", e);
    }

    return mainCategories;
}

   public List<Product> getAllProducts() throws ProductDatabaseException {
    String sql = "SELECT product_id, name, cost_price, profit_margin, tax_rate, final_price, is_expirable, main_category_id, sub_category_id, product_category_id, brand_id, unit_id, product_code FROM product";
    List<Product> products = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            Product product = new Product();
            product.setId(rs.getInt("product_id"));
            product.setName(rs.getString("name"));
            product.setCostPrice(rs.getDouble("cost_price"));
            product.setProfitMargin(rs.getDouble("profit_margin"));
            product.setTaxRate(rs.getDouble("tax_rate"));
            product.setFinalPrice(rs.getDouble("final_price"));
            product.setExpirable(rs.getBoolean("is_expirable"));
            product.setMainCategoryId(rs.getInt("main_category_id"));
            product.setSubcategoryId(rs.getInt("sub_category_id"));
            product.setProductCategoryId(rs.getInt("product_category_id"));
            product.setBrandId(rs.getInt("brand_id"));
            product.setUnitId(rs.getInt("unit_id"));
            product.setProductCode(rs.getString("product_code"));

            products.add(product);
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to retrieve products", e);
    }
    return products;
}

   public void updateProduct(Product product) throws ProductDatabaseException {
    String sql = "UPDATE product SET name = ?, cost_price = ?, profit_margin = ?, tax_rate = ?, is_expirable = ?, main_category_id = ?, sub_category_id = ?, product_category_id = ?, brand_id = ?, unit_id = ?, final_price = ?, product_code = ? WHERE product_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setString(1, product.getName());
        pstmt.setDouble(2, product.getCostPrice());
        pstmt.setDouble(3, product.getProfitMargin());
        pstmt.setDouble(4, product.getTaxRate());
        pstmt.setBoolean(5, product.isExpirable());
        pstmt.setInt(6, product.getMainCategoryId());
        pstmt.setInt(7, product.getSubcategoryId());
        pstmt.setInt(8, product.getProductCategoryId());
        pstmt.setInt(9, product.getBrandId());
        pstmt.setInt(10, product.getUnitId());
        pstmt.setDouble(11, product.getFinalPrice());
        pstmt.setString(12, product.getProductCode()); 
        pstmt.setInt(13, product.getId()); 

        int affectedRows = pstmt.executeUpdate();
        if (affectedRows == 0) {
            throw new ProductDatabaseException("Updating product failed, no rows affected.");
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to update product", e);
    }
}

   public void deleteProduct(int productId) throws ProductDatabaseException {
    String sql = "DELETE FROM product WHERE product_id = ?";  

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, productId);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to delete product", e);
    }
}

    public Product getProductByCode(String productCode) throws ProductDatabaseException {
        String sql = "SELECT * FROM product WHERE product_code = ?";
        Product product = null;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, productCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setCostPrice(rs.getDouble("cost_price"));
                    product.setProfitMargin(rs.getDouble("profit_margin"));
                    product.setTaxRate(rs.getDouble("tax_rate"));
                    product.setFinalPrice(rs.getDouble("final_price"));
                    product.setExpirable(rs.getBoolean("is_expirable"));
                    product.setMainCategoryId(rs.getInt("main_category_id"));
                    product.setSubcategoryId(rs.getInt("sub_category_id"));
                    product.setProductCategoryId(rs.getInt("product_category_id"));
                    product.setBrandId(rs.getInt("brand_id"));
                    product.setUnitId(rs.getInt("unit_id"));
                    product.setProductCode(rs.getString("product_code"));
                }
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Failed to retrieve product with code: " + productCode, e);
        }
        return product;
    }
 
    public boolean isProductExpirable(int productId) throws ProductException {
    String query = "SELECT is_expirable FROM product WHERE product_id = ?";  

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(query)) {

        pstmt.setInt(1, productId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBoolean("is_expirable");
            } else {
                throw new ProductException("Product with ID " + productId + " not found.");
            }
        }
    } catch (SQLException e) {
        throw new ProductException("Error checking if product is expirable.", e);
    }
}

   public double getPriceByProductId(int productId) throws ProductDatabaseException {
    String sql = "SELECT final_price FROM product WHERE product_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, productId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("final_price");
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to retrieve price for product ID: " + productId, e);
    }
    return 0.0;
}
 
    public Product getProductById(int productId) throws ProductDatabaseException {
    String sql = "SELECT * FROM product WHERE product_id = ?";
    Product product = null;

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, productId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setCostPrice(rs.getDouble("cost_price"));
                product.setProfitMargin(rs.getDouble("profit_margin"));
                product.setTaxRate(rs.getDouble("tax_rate"));
                product.setFinalPrice(rs.getDouble("final_price"));
                product.setExpirable(rs.getBoolean("is_expirable"));
                product.setMainCategoryId(rs.getInt("main_category_id"));
                product.setSubcategoryId(rs.getInt("sub_category_id"));
                product.setProductCategoryId(rs.getInt("product_category_id"));
                product.setBrandId(rs.getInt("brand_id"));
                product.setUnitId(rs.getInt("unit_id"));
                product.setProductCode(rs.getString("product_code"));
            } else {
                throw new ProductDatabaseException("Product not found for ID: " + productId);
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to retrieve product with ID: " + productId, e);
    }

    return product;
}

   public List<Product> getAllProductsWithSubCategory() throws ProductDatabaseException {
    String sql = "SELECT p.product_id AS id, p.name AS product_name, p.final_price, " +
                 "s.sub_category_id, s.sub_category_name, pi.image_url " +
                 "FROM product p " +
                 "JOIN sub_category s ON p.sub_category_id = s.sub_category_id " +
                 "LEFT JOIN product_images pi ON p.product_id = pi.product_id";

    List<Product> products = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            Product product = new Product();
            product.setId(rs.getInt("id"));
            product.setName(rs.getString("product_name"));
            product.setFinalPrice(rs.getDouble("final_price"));

            // Subcategory mapping
            Subcategory subCategory = new Subcategory();
            subCategory.setId(rs.getInt("sub_category_id"));
            subCategory.setName(rs.getString("sub_category_name"));
            product.setSubCategory(subCategory);

            // Image mapping
            product.setImageUrl(rs.getString("image_url"));

            products.add(product);
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to retrieve products with subcategory information", e);
    }

    return products;
}

    public Product getProductDetailsForCart(int productId) throws ProductDatabaseException {
        String query = "SELECT product_id, name, cost_price, final_price FROM product WHERE product_id = ?";
        Product product = null;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setCostPrice(rs.getDouble("cost_price"));
                    product.setFinalPrice(rs.getDouble("final_price"));
                } else {
                    throw new ProductDatabaseException("No product found for productId: " + productId);
                }
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Error fetching product details for productId: " + productId, e);
        }

        return product;
    }

    public Map<Integer, Integer> getProductQuantities() throws ProductDatabaseException {
        String query = """
            SELECT product_id, COALESCE(SUM(current_quantity), 0) AS total_quantity
            FROM online_inventory
            GROUP BY product_id
        """;

        Map<Integer, Integer> productQuantities = new HashMap<>();

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                productQuantities.put(rs.getInt("product_id"), rs.getInt("total_quantity"));
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Failed to retrieve product quantities", e);
        }

        return productQuantities;
    }
}

