package main;

public class User {
    private String name;
    private String password;
    private boolean admin;
    private int id = 0;

    public User(String name, String password, boolean admin, int id) {
        this.name = name;
        this.password = password;
        this.admin = admin;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
