package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Controller {

    //***************************************LOGIN*******************************************
    @FXML
    Button loginLoginButton;
    @FXML
    TextField loginUsernameTextField;
    @FXML
    PasswordField loginPasswordPasswordField;

    boolean admin;

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

        Database database = new Database();
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
                stage.setTitle("New Window");
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
            Database database = new Database();
            database.dbConnection();

            try {
                //Make the prepared statement
                ps = database.getConn().prepareStatement(mysqlString);
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

    //USERS
    public void addUserAction(ActionEvent actionEvent) {
        if (admin) {
            try {
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
        }
    }

    public class addUser implements Runnable {
        private String username;
        private String password1;
        private String password2;
        private boolean admin;
        private boolean userAdded = false;
        Database database = new Database();

        public addUser(String username, String password1,String password2, boolean admin) {
            this.username = username;
            this.password1 = password1;
            this.password2 = password2;
            this.admin = admin;
        }

        //Check if the two passwords are the sam
        public boolean passwordTheSame(){
            if(this.password1.equals(this.password2)){
                return true;
            }else return false;
        }

        //Check if user exist
        public boolean userExist() {
            boolean exist = false;
            String mysqlUserExist = "SELECT * FROM login WHERE username=?";
            PreparedStatement ps;
            ResultSet rs;

            try {
                //Prepared Statement
                ps = database.getConn().prepareStatement(mysqlUserExist);
                ps.setString(1, this.username);

                //ResultSet
                rs = ps.executeQuery();
                while (rs.next()) {
                    exist = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return exist;
        }
        //Add User
        public boolean addUser() {
            boolean userAdded = true;
            String mysqlInsert = "INSERT INTO login (username,password,admin) VALUES (?,?,?)";
            try {

                //PreparedStatement
                PreparedStatement ps;
                ps = database.getConn().prepareStatement(mysqlInsert);
                ps.setString(1, this.username);
                ps.setString(2, this.password1);
                ps.setBoolean(3, this.admin);

                //Execute Query
                ps.executeQuery();
            }catch (SQLException e){
                e.printStackTrace();
                userAdded = false;
            }
            return userAdded;
        }

        @Override
        public void run() {
            if(passwordTheSame()){
                return;
            }
            

            if(!userExist()){
                addUser();
                this.userAdded = true;
            }
        }
    }


    public class GetItems implements Runnable {

        @Override
        public void run() {

        }
    }


}
