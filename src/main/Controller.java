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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class Controller {

    //***************************************LOGIN*******************************************
    @FXML
    Button loginLoginButton;
    @FXML
    TextField loginUsernameTextField;
    @FXML
    PasswordField loginPasswordPasswordField;

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

        if(isOK) {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            isLoginOk();

        }
    }

    //**********************************Application********************
    @FXML
    Button applicationRemoveFiltersButton,applicationAddNewItemButton,applicationEditItemButton,applicationRemoveItemsButton,applicationAddUserButton,
            applicationEditUserButton,applicationRemoveUserButton;
}
