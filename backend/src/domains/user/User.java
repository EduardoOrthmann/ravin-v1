package domains.user;

import enums.Role;

public class User {
    private int id;
    private String username;
    private String password;
    private Role role;
    private String token;

    // all args
    public User(int id, String username, String password, Role role, String token) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toString() {
        return "User {\n" +
                "        \tid = " + id + "\n" +
                "        \tusername = '" + username + '\'' + "\n" +
                "        \tpassword = '" + password + '\'' + "\n" +
                "        \trole = " + role + "\n" +
                "        \ttoken = '" + token + '\'' + "\n" +
                '}';
    }
}
