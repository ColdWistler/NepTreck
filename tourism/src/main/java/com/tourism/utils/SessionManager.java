package com.tourism.utils;

import com.tourism.models.User;

/**
 * Session Manager to handle user sessions
 */
public class SessionManager {
    private static User currentUser;
    private static String sessionId;
    private static long sessionStartTime;

    public static void setCurrentUser(User user) {
        currentUser = user;
        sessionId = generateSessionId();
        sessionStartTime = System.currentTimeMillis();

        if (user != null) {
            FileDataManager.logActivity(user.getUsername(), "Session started");
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static long getSessionStartTime() {
        return sessionStartTime;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clearSession() {
        if (currentUser != null) {
            FileDataManager.logActivity(currentUser.getUsername(), "Session ended");
        }

        currentUser = null;
        sessionId = null;
        sessionStartTime = 0;
    }

    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public static boolean isStaff() {
        return currentUser != null && ("STAFF".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()));
    }

    private static String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    public static long getSessionDuration() {
        if (sessionStartTime > 0) {
            return System.currentTimeMillis() - sessionStartTime;
        }
        return 0;
    }

    public static String getSessionDurationString() {
        long duration = getSessionDuration();
        long minutes = duration / (1000 * 60);
        long seconds = (duration % (1000 * 60)) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }
}