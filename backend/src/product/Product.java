package product;

import enums.Role;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Product {
    private static int lastId = 0;
    private int id;
    private String name;
    private String description;
    private String productCode;
    private double costPrice;
    private double salePrice;
    private LocalTime preparationTime;
    private boolean isActive;
    private boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role createdBy;
    private Role updatedBy;

    public Product(String name, String description, String productCode, double costPrice, double salePrice, LocalTime preparationTime, Role createdBy) {
        this.id = ++lastId;
        this.name = name;
        this.description = description;
        this.productCode = productCode;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.preparationTime = preparationTime;
        this.isActive = true;
        this.isAvailable = true;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public Product(int id, String name, String description, String productCode, double costPrice, double salePrice, LocalTime preparationTime, boolean isAvailable, Role updatedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.productCode = productCode;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.preparationTime = preparationTime;
        this.isAvailable = isAvailable;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public LocalTime getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(LocalTime preparationTime) {
        this.preparationTime = preparationTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Role getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Role createdBy) {
        this.createdBy = createdBy;
    }

    public Role getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Role updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "Product {\n" +
                "\tid = " + id + "\n" +
                "\tname = '" + name + '\'' + "\n" +
                "\tdescription = '" + description + '\'' + "\n" +
                "\tproductCode = '" + productCode + '\'' + "\n" +
                "\tcostPrice = " + costPrice + "\n" +
                "\tsalePrice = " + salePrice + "\n" +
                "\tpreparationTime = " + preparationTime + "\n" +
                "\tisActive = " + isActive + "\n" +
                "\tisAvailable = " + isAvailable + "\n" +
                "\tcreatedAt = " + createdAt + "\n" +
                "\tupdatedAt = " + updatedAt + "\n" +
                "\tcreatedBy = " + createdBy + "\n" +
                "\tupdatedBy = " + updatedBy + "\n" +
                '}';
    }
}