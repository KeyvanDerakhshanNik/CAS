package entity;

import javax.persistence.*;

/**
 * Entity class to make Connection with JPA to DB --> user_roles table
 *
 * @author Keyvan Derakhshan Nik
 */
@Entity
@Table(name = "user_roles")
public class User_Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /**
     * user_name for Column user_name
     */
    @Column
    private String user_name;
    /**
     * role_name for Column role_name
     */
    @Column
    private String role_name;

    public User_Roles() {
    }

    public User_Roles(String userName, String roleName) {
        this.user_name = userName;
        this.role_name = roleName;
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
     * getter method for RoleName
     */
    public String getRole_name() {
        return role_name;
    }

    /**
     * setter method for RoleName
     */
    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}
