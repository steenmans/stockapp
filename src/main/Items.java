package main;

import javafx.scene.image.Image;

public class Items {
    private int id = 0;
    private String orderNumber;
    private String name;
    private String info;
    private int minimumToOrder;
    private int inStock;

    public Items(int id, String orderNumber, String name, String info, int minimumToOrder, int inStock) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.name = name;
        this.info = info;
        this.minimumToOrder = minimumToOrder;
        this.inStock = inStock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getMinimumToOrder() {
        return minimumToOrder;
    }

    public void setMinimumToOrder(int minimumToOrder) {
        this.minimumToOrder = minimumToOrder;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }
}
