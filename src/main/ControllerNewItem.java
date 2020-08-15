package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.*;

public class ControllerNewItem {
    @FXML
    private TextField newItemOrderNumberTextField, newItemNameTextField, newItemMinimumToOrderTextField, newItemIntStockTextField, newItemImageTextView;
    @FXML
    private TextArea newItemInfoTextArea;
    @FXML
    private Button newItemBrowsImageButton, newItemSaveButton;

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

    //TODO image add
    public void saveAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);


        if(newItemNameTextField.getText().isEmpty()){
            alert.setContentText("Please Fill in the name");
        }else if(newItemOrderNumberTextField.getText().isEmpty()){
            alert.setContentText("Please Fill in the order number");
        }else if(checkOrderNumber()){
            alert.setContentText("The order Number already exist");
        }else if(checkName()){
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
                    alert.setContentText("New Item added");
                }else alert.setContentText("Something went wrong");

            }catch (SQLException e){
                e.getCause().printStackTrace();
            }
        }










    }

    public boolean checkOrderNumber(){
        boolean orderNumberExist = false;
        String mysqlSelect = "SELECT * FROM items WHERE orderNumber=?";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = MyConnection.getConnection().prepareStatement(mysqlSelect);
            ps.setInt(1,Integer.parseInt(newItemOrderNumberTextField.getText()));

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
}
