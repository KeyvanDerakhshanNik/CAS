package entity;

import javax.persistence.*;

/**
 * Entity class to make Connection with JPA to DB --> users table
 *
 * @author Keyvan Derakhshan Nik
 */
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /**
     * user_name for Column user_name
     */
    @Column
    private String user_name;
    /**
     * user_pass for Column user_pass
     */
    @Column
    private String user_pass;

    public Users() {
    }

    public Users(String userName, String userpass) {

        this.user_name = userName;
        this.user_pass = userpass;
    }

    /**
     * getter method for UserName
     */
    public String getUser_name() {
        return user_name;
    }

    /**
     * setter method for UserName
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**
     * getter method for UserPass
     */
    public String getUser_pass() {
        return user_pass;
    }

    /**
     * setter method for UserPass
     */
    public void setUser_pass(String user_pass) {
        this.user_pass = user_pass;
    }
}
