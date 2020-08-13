package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class ControllerEditUsers {
    @FXML
    private TableView<User> editUserTableView;
    @FXML
    private TableColumn<User, String> editUserTableColumnName;
    @FXML
    private TableColumn<User, Boolean> editUserTableColumnAdmin;
    @FXML
    private TextField editUserNameTextField;
    @FXML
    private PasswordField editUserPassword1PasswordField, editUserPassword2PasswordField;
    @FXML
    private CheckBox editUserAdminCheckbox;

    ObservableList<User> observableListArraylist = FXCollections.observableArrayList();

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

    @FXML
    public void initialize() {
        String mysqlSelect = "SELECT * FROM login";
        Statement st;
        ResultSet rs;

        //data binnenhalen.
        try {
            st = MyConnection.getConnection().createStatement();

            rs = st.executeQuery(mysqlSelect);

            while (rs.next()) {
                observableListArraylist.add(new User(rs.getString("username"), rs.getString("password"), rs.getBoolean("admin")));
            }


        } catch (SQLException e) {
            e.getCause().printStackTrace();
        }


        editUserTableColumnName.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
        editUserTableColumnAdmin.setCellValueFactory(new PropertyValueFactory<User, Boolean>("admin"));
        editUserTableView.setItems(observableListArraylist);

        //Mouse and key
        editUserTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {
                onEdit();
            }
        });
        editUserTableView.setOnKeyReleased((KeyEvent event) -> {
                onEdit();
        });

    }

    public void onEdit() {
        // check the table's selected item and get selected item
        if (editUserTableView.getSelectionModel().getSelectedItem() != null) {
            User user = editUserTableView.getSelectionModel().getSelectedItem();
            editUserNameTextField.setText(user.getName());
            editUserPassword1PasswordField.setText(user.getPassword());
            if(user.isAdmin()){
                editUserAdminCheckbox.setSelected(true);
            }
        }
    }
}




