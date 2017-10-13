package authenticationadmin;

/**
 * CAS Admin Panel has main method, It Uses MVC Pattern
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationAdmin {

    /**
     * Start without Parameter
     */
    public static void main(String[] args) {
        // Start the Program 
        new AuthenticationAdminView().view();
    }
}
