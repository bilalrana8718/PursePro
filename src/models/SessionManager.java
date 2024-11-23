package models;

public class SessionManager {

    // Singleton instance
    private static SessionManager instance;

    // User details
    private int currentUserId;
    private String currentUserName;
    private String currentUserEmail;
    private double currentUserBalance;


    // Private constructor to prevent instantiation
    private SessionManager() {}

    // Static method to get the singleton instance
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Getters and setters for all fields

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
    }
    
    public double getCurrentUserBalance() {
        return currentUserBalance;
    }

    public void setCurrentUserBalance(double currentUserBalance) {
        this.currentUserBalance = currentUserBalance;
    }

    // Clear session data
    public void clearSession() {
        currentUserId = 0;
        currentUserName = null;
        currentUserEmail = null;
        currentUserBalance = 0;
    }
}
