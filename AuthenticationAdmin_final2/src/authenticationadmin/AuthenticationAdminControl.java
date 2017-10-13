package authenticationadmin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JOptionPane;

/**
 * Control Section of Admin Panel
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationAdminControl {

    /**
     * Standard Pattern for UserName Part
     */
    private final String pat = "[A-Za-z][A-Za-z0-9]*";
    /**
     * Standard Pattern for IP Part
     */
    private final String ipPat = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    /**
     * Standard Pattern for Port Part
     */
    private final String portPat = "[0-9]+";

    /**
     * Check Fields ip , port , userName password to validate them locally
     */
    boolean checkFileds(String ip, String port, String userName, String password) {
        if ((userName.matches(pat)) & (userName.length() != 0) & (password.length() != 0) & (ip.matches(ipPat)) & (ip.length() != 0) & (port.matches(portPat)) & (port.length() != 0)) {
            AuthenticationAdminModel model = new AuthenticationAdminModel();
            System.out.println("Let me check with Server OK?!");
            return model.send(ip, port, userName, encryptString(password));
        } else {
            JOptionPane.showMessageDialog(null, "Check fields !!! ");
            return false;
        }
    }

    /**
     * To Encrypt a String (temp) for encryption the password
     */
//    String encrypt(String temp) {
         public String encryptString(String temp)  {

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
     * Check New UserName and Password Standard to add (locally)
     */
    boolean checkNewUser(String newUserName, String newPassword) {
        if ((newUserName.matches(pat)) & (newUserName.length() != 0) & (newPassword.length() != 0)) {
            return true;
        }
        return false;
    }
}
