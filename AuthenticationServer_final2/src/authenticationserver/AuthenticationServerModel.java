package authenticationserver;

import biz.UserFacade;

/**
 * Model Section of Server
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationServerModel {

    /**
     * Check UserName and Password by use Facade method for Communication with
     * DB
     */
    public boolean checkUsernamePassword(String username, String password) {
        if (new UserFacade().findUser(username, password)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find all roles of user by use Facade method for Communication with DB
     */
    public String findRoles(String user) {
        return new UserFacade().findRoles(user);
    }

    /**
     * add UserName and Password and Role by use Facade method for Communication
     * with DB
     */
    public String addUser(String u, String p, String r) {
        return new UserFacade().addUser(u, p, r);
    }
}
