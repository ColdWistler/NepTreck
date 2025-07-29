package com.tourism.services;

import com.tourism.models.User;
import com.tourism.utils.FileDataManager;
import com.tourism.utils.SessionManager;

/**
 * Authentication Service for user login and validation
 */
public class AuthenticationService {

    public boolean authenticateUser(String username, String password) {
        try {
            User user = FileDataManager.findUserByUsername(username);

            if (user != null && user.getPassword().equals(password) && user.isActive()) {
                SessionManager.setCurrentUser(user);
                FileDataManager.logActivity(username, "User authenticated successfully");
                return true;
            } else {
                FileDataManager.logActivity(username, "Authentication failed");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            FileDataManager.logActivity(username, "Authentication error: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        try {
            User user = FileDataManager.findUserByUsername(username);

            if (user != null && user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                boolean saved = FileDataManager.saveUser(user);

                if (saved) {
                    FileDataManager.logActivity(username, "Password changed successfully");
                } else {
                    FileDataManager.logActivity(username, "Failed to save new password");
                }

                return saved;
            } else {
                FileDataManager.logActivity(username, "Password change failed - invalid old password");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Password change error: " + e.getMessage());
            FileDataManager.logActivity(username, "Password change error: " + e.getMessage());
            return false;
        }
    }

    public boolean validateUserCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    public void logout() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            FileDataManager.logActivity(currentUser.getUsername(), "User logged out");
        }
        SessionManager.clearSession();
    }

    public boolean isUserLoggedIn() {
        return SessionManager.isLoggedIn();
    }

    public boolean hasAdminPrivileges() {
        return SessionManager.isAdmin();
    }

    public boolean hasStaffPrivileges() {
        return SessionManager.isStaff();
    }
}