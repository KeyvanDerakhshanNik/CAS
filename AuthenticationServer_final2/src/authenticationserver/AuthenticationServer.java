package authenticationserver;

/**
 * CAS Server has main method, It Uses MC and Facade Pattern and JPA
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationServer {

    /**
     * Start without Parameter
     */
    public static void main(String[] args) {
        // Start the Program
        new AuthenticationServerControl().listen();
    }
}
