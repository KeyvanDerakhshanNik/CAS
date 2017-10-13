package authenticationclient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JOptionPane;

/**
 * Control Section of Client Panel
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationClientControl {

    /**
     * Standard Pattern for UserName Part
     */
    private final String pat = "[A-Za-z][A-Za-z0-9]*";

    /**
     * Verify userName and password
     */
    boolean verify(String userName, String password) {
        if ((userName.matches(pat)) & (userName.length() != 0) & (password.length() != 0)) {
            AuthenticationClientModel model = new AuthenticationClientModel();
            // create user Object and send its reference to model section
            model.send(createUser(userName, encrypt(password)));
            return true;
        }
        JOptionPane.showMessageDialog(null, "Your User Name  or Password has illegal charachter !!! ");
        return false;
    }

    /**
     * To Encrypt a String (temp) for encryption the password
     */
    String encrypt(String temp) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(temp.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "error";
        }
    }

    /**
     * Create User Object.
     */
    User createUser(String u, String p) {
        User tempUser = new User();
        tempUser.setUserNameString(u);
        tempUser.setPasswordString(p);
        return tempUser;
    }
}
