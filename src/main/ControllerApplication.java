package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class ControllerApplication {

    //TODO nu gewoon true gezet
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

    public void saveAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Items item;
        PreparedStatement ps;

        if((item = applicationTableViewItems.getSelectionModel().getSelectedItem()) != null){

            String mysqlUpdate = "UPDATE items SET orderNumber=?, name=?, info=?, minimum_to_order=?, in_stock=? " +
                    "WHERE id=?";
            try {
                ps = MyConnection.getConnection().prepareStatement(mysqlUpdate);
                ps.setString(1, applicationOrderNumberTextField.getText());
                ps.setString(2, applicationNameTextField.getText());
                ps.setString(3, applicationInfoTextArea.getText());
                ps.setInt(4, Integer.parseInt(applicationMinToOrderTextField.getText()));
                ps.setInt(5, Integer.parseInt(applicationInStockTextField.getText()));
                ps.setInt(6, item.getId());

                boolean updateOk = ps.execute();

                //Als ok vernieuw de tableView
                if(!updateOk){
                    alert.setContentText("Item updated");
                    itemsObservableList = getItemsFromSql();
                    applicationTableViewItems.setItems(itemsObservableList);
                }else {
                    alert.setContentText("Something went wrong");
                }
            }catch (SQLException e){
                e.getCause().printStackTrace();
            }

        }else {
            alert.setContentText("Please Select an Item");
        }

        alert.show();
    }

    public void addNewItemAction(ActionEvent actionEvent) {
        if (admin) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../gui/newItem.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 400, 400);
                Stage stage = new Stage();
                stage.setTitle("New Item");
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
    private TextField applicationNameTextField,applicationOrderNumberTextField,applicationInStockTextField,applicationMinToOrderTextField, applicationSearchTextField;
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

        //Search
        Search search = new Search();
        search.start();







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

    public void refreshTableView(){
        itemsObservableList = getItemsFromSql();
        applicationTableViewItems.setItems(itemsObservableList);
    }

    public void onEdit() {
        // check the table's selected item and get selected item
        if (applicationTableViewItems.getSelectionModel().getSelectedItem() != null) {
            Items items = applicationTableViewItems.getSelectionModel().getSelectedItem();

            //Items
            applicationNameTextField.setText(items.getName());
            applicationOrderNumberTextField.setText(items.getOrderNumber());
            applicationInStockTextField.setText(String.valueOf(items.getInStock()));
            applicationMinToOrderTextField.setText(String.valueOf(items.getMinimumToOrder()));
            applicationInfoTextArea.setText(items.getInfo());

            //Image
            File file = new File("C://Users/samst/OneDrive/Documents/stockapp/" + items.getId() + ".jpg");
            Image image = new Image(file.toURI().toString());
            applicationItemsImageView.setImage(image);

        }
    }


    //Search
    //TODO moet niet blijven verversen,alleen als de text verandert,while moet veranderen.
    public class Search extends Thread{
        boolean sameWord = true;

        public void run(){
            while (true){
                if(!applicationSearchTextField.getText().isEmpty()){
                    applicationTableViewItems.setItems(filteredList(itemsObservableList,applicationSearchTextField.getText()));
                }else applicationTableViewItems.setItems(itemsObservableList);
            }
        }
    }

    private boolean searchFindItems(Items items,String searchText){
        return items.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                items.getOrderNumber().toLowerCase().contains(searchText.toLowerCase()) ||
                items.getInfo().toLowerCase().contains(searchText.toLowerCase());
    }

    private ObservableList<Items> filteredList (List<Items> items,String searchText){
        ObservableList<Items> filteredList = FXCollections.observableArrayList();

        for(Items items1 : items){
            if(searchFindItems(items1,searchText)){
                filteredList.add(items1);
            }
        }
        return filteredList;
    }




}
