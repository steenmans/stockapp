package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    @FXML
    Button editUserSaveButton;

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
        String mysqlSelect = "SELECT * FROM users";
        Statement st;
        ResultSet rs;

        //data binnenhalen.
        try {
            st = MyConnection.getConnection().createStatement();

            rs = st.executeQuery(mysqlSelect);

            while (rs.next()) {
                observableListArraylist.add(new User(rs.getString("username"), rs.getString("password"), rs.getBoolean("admin"), rs.getInt("id")));
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
            if (user.isAdmin()) {
                editUserAdminCheckbox.setSelected(true);
            }
        }
    }

    //Save Button pressed
    public void editUserSaveButtonAction(ActionEvent actionEvent) {
        String username = editUserNameTextField.getText();
        String password1 = editUserPassword1PasswordField.getText();
        String password2 = editUserPassword2PasswordField.getText();
        boolean admin = editUserAdminCheckbox.isSelected();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);


        if (editUserTableView.getSelectionModel().getSelectedItem() != null) {
            User userOud = editUserTableView.getSelectionModel().getSelectedItem();

            if (!password1.equals(password2)) {
                alert.setContentText("The passwords dont match");
                alert.show();
            } else if (username.isEmpty()) {
                alert.setContentText("The username is empty");
                alert.show();
            } else {
                String mysqlUpdate = "UPDATE users SET username=?, password=?, admin=? WHERE id=?";
                PreparedStatement ps;
                try {
                    ps = MyConnection.getConnection().prepareStatement(mysqlUpdate);
                    ps.setString(1, username);
                    ps.setString(2, password1);
                    ps.setBoolean(3, admin);
                    ps.setInt(4, userOud.getId());


                    //Als het profiel is ge updated in de databank,word de tableview geupdate
                    if (!ps.execute()) {
                        alert.setContentText("Updated Profile");
                        Statement st;
                        ResultSet rs;
                        String mysqlSelect = "SELECT * FROM users";

                        st = MyConnection.getConnection().createStatement();
                        rs = st.executeQuery(mysqlSelect);

                        //clean list
                        observableListArraylist.clear();

                        while (rs.next()) {
                            //Fill the lst with the new users
                            observableListArraylist.add(new User(rs.getString("username"), rs.getString("password"), rs.getBoolean("admin"), rs.getInt("id")));
                        }

                        //Set the tableView
                        editUserTableView.setItems(observableListArraylist);

                    } else {
                        alert.setContentText("Update Failed");
                    }
                    alert.show();

                } catch (SQLException e) {
                    e.getCause().printStackTrace();
                }
            }


        } else {
            alert.setContentText("Please Select a User");
            alert.show();


        }
    }


}




