package security.model;

/**
 * Created by kasra on 5/27/2017.
 */
public class AccountCredentials {

    private String username;
    private String password;
    private String role;

    public AccountCredentials(String username, String role) {
        this.username = username;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}