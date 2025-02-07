/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

/**
 *
 * @author User
 */

public class Product {
    private int id;  
    private String name;
    private int productCategoryId;
    private int brandId;
    private int unitId;
    private double costPrice;
    private double profitMargin;
    private double taxRate;
    private double finalPrice;
    private String productCode;
    private boolean isExpirable;
    private String createdBy;
    private int mainCategoryId;
    private int subcategoryId;  
    private String imageUrl;
     private Subcategory subCategory;

    public Product() {}


    public int getId() {
            return id;
        }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(double profitMargin) {
        this.profitMargin = profitMargin;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public boolean isExpirable() {
        return isExpirable;
    }

    public void setExpirable(boolean isExpirable) {
        this.isExpirable = isExpirable;
    }

    public String getCreatedBy() {
        return createdBy;
    }
    public int getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(int subcategoryId) {
        this.subcategoryId = subcategoryId;
    }
     public int getMainCategoryId() {
        return mainCategoryId;
    }

    public void setMainCategoryId(int mainCategoryId) {
        this.mainCategoryId = mainCategoryId;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void calculateFinalPrice() {
        this.finalPrice = this.costPrice * (1 + this.profitMargin / 100) * (1 + this.taxRate / 100);
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Subcategory getSubCategory() {
    return subCategory;
    }

    public void setSubCategory(Subcategory subCategory) {
        this.subCategory = subCategory;
    }


}

