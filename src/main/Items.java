package main;

import javafx.scene.image.Image;

public class Items {
    private int id = 0;
    private String orderNumber;
    private String name;
    private String info;
    private int minimumToOrder;
    private int inStock;
    private Image image = null;
    private String imageName;

    //zonder image
    public Items(int id, String orderNumber, String name, String info, int minimumToOrder, int inStock) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.name = name;
        this.info = info;
        this.minimumToOrder = minimumToOrder;
        this.inStock = inStock;
    }

    //Met image
    public Items(int id, String orderNumber, String name, String info, int minimumToOrder, int inStock, Image image, String imageName) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.name = name;
        this.info = info;
        this.minimumToOrder = minimumToOrder;
        this.inStock = inStock;
        this.image = image;
        this.imageName = imageName;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
