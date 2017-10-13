package authenticationserver;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Entity class to make XML File
 *
 * @author Keyvan Derakhshan Nik
 *
 * to create XML Tag
 */
@XmlRootElement(name = "request")
//  to order XML Tag
@XmlType(propOrder = {"command", "userNameString", "passwordString"})
public class User implements Serializable {

    User() {
    }

    User(String u, String p, String c) {
        setUserNameString(u);
        setPasswordString(p);
        setCommand(c);
    }

    private String command;

    private String userNameString;

    private String passwordString;

    /**
     * getter method for userNameString
     */
    public String getUserNameString() {
        return userNameString;
    }

    /**
     * setter method for userNameString
     */
    @XmlElement
    // to make user-name Tag
    public void setUserNameString(String userNameString) {
        this.userNameString = userNameString;
    }

    /**
     * getter method for passwordString
     */
    public String getPasswordString() {
        return passwordString;
    }

    /**
     * setter method for password
     */
    @XmlElement
    // to make password Tag
    public void setPasswordString(String passwordString) {
        this.passwordString = passwordString;
    }

    /**
     * getter method for command
     */
    public String getCommand() {
        return command;
    }

    /**
     * setter method for command
     */
    @XmlElement
    // to make command Tag 

    public void setCommand(String command) {
        this.command = command;
    }
}
