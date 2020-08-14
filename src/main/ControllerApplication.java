package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ControllerApplication {

    boolean admin = true;


    //********USERS*****************
    //******NewUser Screen
    public void addUserAction(ActionEvent actionEvent) {
        if (admin) {
            try {
                System.out.println("Opening addUser");
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../gui/addUser.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 400, 400);
                Stage stage = new Stage();
                stage.setTitle("New User");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You are not Admin");
        }
    }

    @FXML
    Button addUserAddUserButton;
    @FXML
    TextField addUserUserNameTextfield;
    @FXML
    PasswordField addUserPassword1PasswordField, addUserPassword2PasswordField;
    @FXML
    CheckBox addUserAdminCheckbox;

    //Users
    public boolean checkUsername(String username) {

        boolean checkUser = false;
        String query = "SELECT * FROM users WHERE username =?";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = ControllerLogin.MyConnection.getConnection().prepareStatement(query);
            ps.setString(1, username);

            rs = ps.executeQuery();

            if (rs.next()) {
                checkUser = true;
            }
        } catch (SQLException e) {
            e.getCause().printStackTrace();
        }
        return checkUser;
    }

    public void newUserAddAction(ActionEvent actionEvent) {
        String username = addUserUserNameTextfield.getText();
        String password1 = addUserPassword1PasswordField.getText();
        String password2 = addUserPassword2PasswordField.getText();
        boolean admin = false;
        if (addUserAdminCheckbox.isSelected()) {
            admin = true;
        }


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String mysqlInsert = "INSERT INTO users (username,password,admin) VALUES (?,?,?)";

        if (username.equals("")) {
            alert.setContentText("Please fill in a username");
            alert.show();
        } else if (password1.equals("")) {
            alert.setContentText("Please fill in a password");
            alert.show();
        } else if (!password1.equals(password2)) {
            alert.setContentText("Please retype the password");
            alert.show();
        } else if (checkUsername(username)) {
            alert.setContentText("Username already exist");
            alert.show();
        } else {

            PreparedStatement ps;
            ResultSet rs;

            try {
                ps = ControllerLogin.MyConnection.getConnection().prepareStatement(mysqlInsert);
                ps.setString(1, username);
                ps.setString(2, password1);
                ps.setBoolean(3, admin);

                if (ps.executeUpdate() > 0) {
                    alert.setContentText("Username added");
                    alert.show();
                }

            } catch (SQLException e) {
                e.getCause().printStackTrace();
            }

        }
    }


    //Buttons
    public void openEditUserAction(ActionEvent actionEvent) {
        if (admin) {
            try {
                System.out.println("Opening editUser");
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../gui/editUser.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 400, 400);
                Stage stage = new Stage();
                stage.setTitle("Edit User");
                stage.setScene(scene);
                stage.show();


            } catch (Exception e) {
                e.getCause().printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You are not Admin");
        }
    }
    //TODO
    public void saveAction(ActionEvent actionEvent) {

    }

    public class GetItems implements Runnable {

        @Override
        public void run() {

        }
    }

    //******************************************************************************************************************
    //******************************************************************************************************************
    //*************************************************TableViewItems***************************************************
    @FXML
    private TableView<Items> applicationTableViewItems;
    @FXML
    private TableColumn<Items, Integer> applicationTableColumnOrderNumber, applicationTableColumnInStock, applicationTableColumnMin;
    @FXML
    private TableColumn<Items, String> applicationTableColumnName;
    @FXML
    private TextField applicationNameTextField,applicationOrderNumberTextField,applicationInStockTextField,applicationMinToOrderTextField;
    @FXML
    private TextArea applicationInfoTextArea;
    @FXML
    private ImageView applicationItemsImageView;
    ObservableList<Items> itemsObservableList = FXCollections.observableArrayList();

    public void initialize() {

        applicationTableColumnOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        applicationTableColumnOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        applicationTableColumnInStock.setCellValueFactory(new PropertyValueFactory<>("inStock"));
        applicationTableColumnMin.setCellValueFactory(new PropertyValueFactory<>("minimumToOrder"));
        applicationTableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        itemsObservableList = getItemsFromSql();

        applicationTableViewItems.setItems(itemsObservableList);

        //Mouse and key
        applicationTableViewItems.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {
                onEdit();
            }
        });
        applicationTableViewItems.setOnKeyReleased((KeyEvent event) -> {
            onEdit();
        });






    }
    public ObservableList<Items> getItemsFromSql() {
        ObservableList<Items> observableList = FXCollections.observableArrayList();

        String mysqlSelect = "SELECT * FROM items";
        Statement st;
        ResultSet rs;

        try {
            st = ControllerLogin.MyConnection.getConnection().createStatement();
            rs = st.executeQuery(mysqlSelect);

            while (rs.next()) {
                Items items = new Items(rs.getInt("id"),
                        rs.getString("orderNumber"),
                        rs.getString("name"),
                        rs.getString("info"),
                        rs.getInt("minimum_to_order"),
                        rs.getInt("in_stock"));

                observableList.add(items);
            }
        } catch (SQLException e) {
            e.getCause().printStackTrace();
        }

        return observableList;

    }

    public void onEdit() {
        // check the table's selected item and get selected item
        if (applicationTableViewItems.getSelectionModel().getSelectedItem() != null) {
            Items items = applicationTableViewItems.getSelectionModel().getSelectedItem();

            applicationNameTextField.setText(items.getName());
            applicationOrderNumberTextField.setText(items.getOrderNumber());
            applicationInStockTextField.setText(String.valueOf(items.getInStock()));
            applicationMinToOrderTextField.setText(String.valueOf(items.getMinimumToOrder()));
            applicationInfoTextArea.setText(items.getInfo());

        }
    }


}
