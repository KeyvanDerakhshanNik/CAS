package authenticationserver;

import java.util.Date;

/**
 * Entity class to make Session
 *
 * @author Keyvan Derakhshan Nik
 */
public class Session {

    /**
     * Keep SessionID
     */
    private String sessionId;
    /**
     * Keep UserName
     */
    private String userName;
    /**
     * Keep Role
     */
    private String role;
    /**
     * Keep Creation Time
     */
    private long creationTime;
    /**
     * Keep Last Reference Time
     */
    private long lastReferenceTime;

    Session() {
    }

    Session(String sId, String u, String role, long lastTime) {
        setSessionId(sId);
        setUserName(u);
        setRole(role);
        setCreationTime(new Date().getTime() / 60000);
        setLastReferenceTime(lastTime);
    }

    /**
     * getter for Session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * setter for Session ID
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * getter for UserName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * setter for UserName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * getter for Role
     */
    public String getRole() {
        return role;
    }

    /**
     * setter for Role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * getter for Creation Time
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * setter for Creation Time
     */
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * getter for Last Reference Time
     */
    public long getLastReferenceTime() {
        return lastReferenceTime;
    }

    /**
     * setter for Last Reference Time
     */
    public void setLastReferenceTime(long lastReferenceTime) {
        this.lastReferenceTime = lastReferenceTime;
    }
}
