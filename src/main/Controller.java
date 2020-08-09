package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.xml.stream.FactoryConfigurationError;
import java.io.IOException;
import java.sql.*;


public class Controller {

    //***************************************LOGIN*******************************************
    @FXML
    Button loginLoginButton;
    @FXML
    TextField loginUsernameTextField;
    @FXML
    PasswordField loginPasswordPasswordField;

    static boolean admin = false;



    //Connection
    public static class MyConnection {

        // create a function to connect with mysql database
        public static Connection getConnection(){

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

    //Login
    public void loginButton(ActionEvent actionEvent) throws InterruptedException {
        String username;
        String password;

        System.out.println("Button pushed");


        //Check if username is empty
        if (loginUsernameTextField.getText().isEmpty()) {
            return;
        } else username = loginUsernameTextField.getText();
        //Check if password is empty
        if (loginPasswordPasswordField.getText().isEmpty()) {
            return;
        } else password = loginPasswordPasswordField.getText();

        UserLogin userLogin = new UserLogin(username, password);
        System.out.println("username + password not empty");

        Thread thread = new Thread(userLogin);
        //Start thread
        thread.start();
        //Wait on thread
        thread.join();
        boolean isOK = userLogin.isLoginOk();
        System.out.println("value:" + isOK);
        System.out.println("Admin:" + admin);

        if (isOK) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../gui/application.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
                Stage stage = new Stage();
                stage.setTitle("Main application");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //Login Class
    public class UserLogin implements Runnable {
        final String username;
        final String password;
        String mysqlString = "SELECT * FROM login where username=? and password=?";
        Connection connection = null;
        PreparedStatement ps;
        ResultSet rs;
        private boolean loginOk = false;

        //Constructor
        public UserLogin(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public boolean isLoginOk() {
            return loginOk;
        }

        @Override
        public void run() {


            try {
                //Make the prepared statement
                ps = MyConnection.getConnection().prepareStatement(mysqlString);
                ps.setString(1, this.username);
                ps.setString(2, this.password);

                //Execute the statement and read it in a resultset
                rs = ps.executeQuery();

                System.out.println("Execute querry:" + ps.toString());

                while (rs.next()) {
                    loginOk = true;
                    System.out.println("gevonden");
                    Controller.this.admin = rs.getBoolean("admin");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    //**********************************Application********************
    @FXML
    Button applicationRemoveFiltersButton, applicationAddNewItemButton, applicationEditItemButton, applicationRemoveItemsButton, applicationAddUserButton,
            applicationEditUserButton, applicationRemoveUserButton;

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
        }else {
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
        String query = "SELECT * FROM login WHERE username =?";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = MyConnection.getConnection().prepareStatement(query);
            ps.setString(1, username);

            rs = ps.executeQuery();

            if(rs.next())
            {
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
        String mysqlInsert = "INSERT INTO login (username,password,admin) VALUES (?,?,?)";

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
        }

        else {

            PreparedStatement ps;
            ResultSet rs;

            try {
                ps = MyConnection.getConnection().prepareStatement(mysqlInsert);
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

    //*****Edit User
    @FXML
    TableView editUserTableView;
    @FXML
    TableColumn editUserTableColumnUsername, editUserTableColumnAdmin;
    @FXML
    TextField editUserNameTextField,editUserPassword2PaswordField;
    @FXML
    CheckBox editUserAdminCheckbox;
    @FXML
    Button editUserSaveButton;




    ObservableList<User> observableArrayList = FXCollections.observableArrayList();
    //Open
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

                //fill the obervableArraylist
                getUsersIntoObservableArraylist();
                User user = observableArrayList.get(0);
                System.out.println("Username:" + user.getUsername());
                //Show them
                //TODO werkt niet
                ///setUsersInEditUsersTableview();


            } catch (Exception e) {
                e.getCause().printStackTrace();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You are not Admin");
        }
    }


    public class User{
        String username;
        String password;
        boolean admin = false;

        public User(String username, String password,boolean admin) {
            this.username = username;
            this.password = password;
            this.admin = admin;
        }



        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password1) {
            this.password = password1;
        }

        public boolean getAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }
    }
    public void getUsersIntoObservableArraylist(){
        String mysqlSelect = "SELECT * FROM login";
        Statement st;
        ResultSet rs;

        try {
            st = MyConnection.getConnection().createStatement();
            rs = st.executeQuery(mysqlSelect);

            while (rs.next()){
                String username = rs.getString("username");
                String password = rs.getString("password");
                boolean admin = rs.getBoolean("admin");

                User user = new User(username,password,admin);
                observableArrayList.add(user);
            }
        }catch (SQLException e){
            e.getCause().printStackTrace();
        }


    }
    public void setUsersInEditUsersTableview(){
        editUserTableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        //editUserTableColumnAdmin.setCellValueFactory(new PropertyValueFactory<>("admin"));

        editUserTableView.setItems(observableArrayList);

    }







    public class GetItems implements Runnable {

        @Override
        public void run() {

        }
    }


}
