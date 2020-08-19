package main;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class ControllerNewItem {
    @FXML
    private TextField newItemOrderNumberTextField, newItemNameTextField, newItemMinimumToOrderTextField, newItemIntStockTextField, newItemImageTextView;
    @FXML
    private TextArea newItemInfoTextArea;
    @FXML
    private Button newItemBrowsImageButton, newItemSaveButton;

    File file = null;


    //Browse the Image
    public void browseAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.jpg"));

        file = fileChooser.showOpenDialog(null);

        if(file != null){
            newItemImageTextView.setText(file.getAbsolutePath());
        }

    }

    //Connection
    public static class MyConnection {

        // create a function to connect with mysql database
        public static Connection getConnection() {

            Connection con = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://steenmans.synology.me/stock?useSSL=false", "steenmans", "Marlboro5419!");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            return con;
        }

    }

    public void saveAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);


        if(newItemNameTextField.getText().isEmpty()){
            alert.setContentText("Please Fill in the name");
        }else if(newItemOrderNumberTextField.getText().isEmpty()){
            alert.setContentText("Please Fill in the order number");
        }else if(checkOrderNumber()){
            alert.setContentText("The order Number already exist");
        }else if(checkName()) {
            alert.setContentText("The name already exist");
        }else {
            String mysqlInsert = "INSERT INTO items (orderNumber,name,info,minimum_to_order,in_stock) " +
                    "VALUES (?,?,?,?,?)";
            PreparedStatement ps;

            try {
                ps = MyConnection.getConnection().prepareStatement(mysqlInsert);

                ps.setString(1,newItemOrderNumberTextField.getText());
                ps.setString(2,newItemNameTextField.getText());
                ps.setString(3,newItemInfoTextArea.getText());
                ps.setInt(4,Integer.parseInt(newItemMinimumToOrderTextField.getText()));
                ps.setInt(5,Integer.parseInt(newItemIntStockTextField.getText()));

                Boolean queryComplete = ps.execute();

                if (!queryComplete){

                    //Save the Image
                    if(file != null){
                        int id = getIdFromSql(newItemOrderNumberTextField.getText());
                        System.out.println("File:" + file.toURI().toString());
                        Image image = new Image(file.toURI().toString());
                        System.out.println("ID:" + id);

                        File fileToWrite = new File("C://Users/samst/OneDrive/Documents/stockapp/" + id + ".jpg");


                        try {
                            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpg", fileToWrite);
                            file = null;
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    alert.setContentText("New Item added");




                }else alert.setContentText("Something went wrong");

            }catch (SQLException e){
                e.getCause().printStackTrace();
            }

        }
        alert.show();
    }

    public boolean checkOrderNumber(){
        boolean orderNumberExist = false;
        String mysqlSelect = "SELECT * FROM items WHERE orderNumber=?";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = MyConnection.getConnection().prepareStatement(mysqlSelect);
            ps.setString(1,newItemOrderNumberTextField.getText());

            rs = ps.executeQuery();

            while (rs.next()){
                orderNumberExist = true;
            }
        }catch (SQLException e){
            e.getCause().printStackTrace();
        }
        return orderNumberExist;
    }

    public boolean checkName(){
        boolean nameExist = false;
        String mysqlSelect = "SELECT * FROM items WHERE name=?";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = MyConnection.getConnection().prepareStatement(mysqlSelect);
            ps.setString(1,newItemNameTextField.getText());

            rs = ps.executeQuery();

            while (rs.next()){
                nameExist = true;
            }
        }catch (SQLException e){
            e.getCause().printStackTrace();
        }
        return nameExist;
    }

    public int getIdFromSql(String orderNumber){
        String mysqlSelect = "SELECT id FROM items WHERE orderNumber = ?";
        PreparedStatement ps;
        ResultSet rs;
        int id = 0;

        try {
            ps = MyConnection.getConnection().prepareStatement(mysqlSelect);
            ps.setString(1,orderNumber);

            rs = ps.executeQuery();

            while (rs.next()){
                id = rs.getInt("id");
            }
        }catch (SQLException e){
            e.getCause().printStackTrace();
        }

        return id;
    }
}
