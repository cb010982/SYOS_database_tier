/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.dao;

/**
 *
 * @author User
 */
public class ReportQueries {
    //ENDOFDAYREPORT QUERRIES
    public static final String GET_ONLINE_SALES_REPORT = 
        "SELECT p.product_code, p.name, " +
        "COALESCE(SUM(ci.quantity), 0) AS online_quantity_sold, " +
        "COALESCE(SUM(ci.price * ci.quantity), 0) AS online_revenue " +
        "FROM product p " +
        "LEFT JOIN cart_items ci ON p.product_id = ci.product_id AND DATE(ci.created_at) = CURRENT_DATE " +
        "GROUP BY p.product_id, p.product_code, p.name " +
        "ORDER BY p.name;";

    public static final String GET_SHELF_SALES_REPORT = 
        "SELECT p.product_code, p.name, " +
        "COALESCE(SUM(bi.quantity), 0) AS shelf_quantity_sold, " +
        "COALESCE(SUM(bi.price * bi.quantity), 0) AS shelf_revenue " +
        "FROM product p " +
        "LEFT JOIN bill_items bi ON p.product_id = bi.product_id AND DATE(bi.created_at) = CURRENT_DATE " +
        "GROUP BY p.product_id, p.product_code, p.name " +
        "ORDER BY p.name;";

    public static final String GET_ONLINE_SALES_REVENUE = 
        "SELECT COALESCE(SUM(c.net_total), 0) AS online_sales_revenue " +
        "FROM carts c WHERE DATE(c.created_at) = CURRENT_DATE;";

   
    public static final String GET_INSTORE_SALES_REVENUE = 
        "SELECT COALESCE(SUM(b.net_total), 0) AS instore_sales_revenue " +
        "FROM bills b WHERE DATE(b.created_at) = CURRENT_DATE;";
    
    
    //RESHELVE REPORT
    public static final String GET_SHELF_RESHELVE_REPORT = 
        "SELECT p.product_code, p.name, " +
        "(s.max_capacity - s.current_quantity) AS reshelve_amount " +
        "FROM product p " +
        "JOIN shelf s ON p.product_id = s.product_id " +
        "WHERE (s.max_capacity - s.current_quantity) > 0;";
    
    public static final String GET_ONLINE_RESHELVE_REPORT = 
        "SELECT p.product_code, p.name, " +
        "(oi.max_capacity - oi.current_quantity) AS reshelve_amount " +
        "FROM product p " +
        "JOIN online_inventory oi ON p.product_id = oi.product_id " +
        "WHERE (oi.max_capacity - oi.current_quantity) > 0;";

    //REORDER REPORT
    public static final String GET_REORDER_REPORT = 
        "SELECT p.product_code, p.name, mc.main_category_name, COUNT(si.product_id) AS current_quantity " +
        "FROM product p " +
        "JOIN main_category mc ON p.main_category_id = mc.main_category_id " +
        "LEFT JOIN store_inventory si ON p.product_id = si.product_id " +
        "GROUP BY p.product_code, p.name, mc.main_category_name, p.main_category_id " +
        "HAVING ( " +
        "    (p.main_category_id IN (1, 3, 9, 10, 5, 11) AND COUNT(si.product_id) < 50) OR " +
        "    (p.main_category_id IN (2, 7) AND COUNT(si.product_id) < 30) OR " +
        "    (p.main_category_id IN (6, 8) AND COUNT(si.product_id) < 20) " +
        ") " +
        "ORDER BY mc.main_category_name, p.name LIMIT 0, 25;";

    //STOCK REPORT
    public static final String GET_STORE_INVENTORY_BATCHES = 
        "SELECT b.batch_id, p.name AS product_name, p.product_code, b.date_received AS purchase_date, " +
        "b.purchased_quantity, COUNT(si.item_serial_number) AS quantity_available, b.expiry_date " +
        "FROM store_inventory si " +
        "JOIN batches b ON si.batch_id = b.batch_id " +
        "JOIN product p ON b.product_id = p.product_id " +
        "GROUP BY b.batch_id, p.product_code, b.date_received, b.expiry_date, b.purchased_quantity " +
        "ORDER BY p.product_code, b.date_received;";

    public static final String GET_ONLINE_INVENTORY_BATCHES = 
        "SELECT b.batch_id, p.name AS product_name, p.product_code, b.date_received AS purchase_date, " +
        "b.purchased_quantity, COUNT(oi.item_serial_number) AS quantity_available, b.expiry_date " +
        "FROM online_inventory_items oi " +
        "JOIN batches b ON oi.batch_id = b.batch_id " +
        "JOIN product p ON oi.product_id = p.product_id " +
        "GROUP BY b.batch_id, p.product_code, b.date_received, b.expiry_date, b.purchased_quantity " +
        "ORDER BY p.product_code, b.date_received;";

    public static final String GET_SHELF_INVENTORY_BATCHES = 
        "SELECT b.batch_id, p.name AS product_name, p.product_code, b.date_received AS purchase_date, " +
        "b.purchased_quantity, COUNT(si.item_serial_number) AS quantity_available, b.expiry_date " +
        "FROM shelf_items si " +
        "JOIN batches b ON si.batch_id = b.batch_id " +
        "JOIN product p ON si.product_id = p.product_id " +
        "GROUP BY b.batch_id, p.product_code, b.date_received, b.expiry_date, b.purchased_quantity " +
        "ORDER BY p.product_code, b.date_received;";

    //BILL REPORTS
    public static final String GET_BILL_ITEMS = 
        "SELECT p.name AS product_name, p.product_code, bi.quantity, bi.price " +
        "FROM bill_items bi " +
        "JOIN product p ON bi.product_id = p.product_id " +
        "WHERE bi.bill_id = (SELECT bill_id FROM bills WHERE bill_no = ?)";

    public static final String GET_CART_ITEMS = 
        "SELECT p.name AS product_name, p.product_code, ci.quantity, ci.price " +
        "FROM cart_items ci " +
        "JOIN product p ON ci.product_id = p.product_id " +
        "WHERE ci.cart_id = (SELECT cart_id FROM carts WHERE cart_no = ?)";

    public static final String GET_BILL_TRANSACTIONS = 
        "SELECT b.bill_no, b.date, b.gross_total, b.discount, b.net_total, b.cash_received, b.change_given, " +
        "b.payment_type " +
        "FROM bills b " +
        "ORDER BY b.date, b.bill_no;";

    public static final String GET_CART_TRANSACTIONS = 
        "SELECT c.cart_no, c.date, c.gross_total, c.discount, c.net_total, c.telephone, c.address " +
        "FROM carts c " +
        "ORDER BY c.date, c.cart_no;";


}
