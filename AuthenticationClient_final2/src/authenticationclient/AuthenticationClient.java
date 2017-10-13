package authenticationclient;

/**
 * CAS Client Panel has main method, It Uses MVC Pattern
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationClient {

    /**
     * Start without Parameter
     */
    public static void main(String[] args) {
        // Start the Program 
        new AuthenticationClientView().view();
    }
}
