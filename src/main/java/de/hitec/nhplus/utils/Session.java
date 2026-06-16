package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.User;

public class Session {
    private static User currentUser;

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static boolean isAdmin() { return currentUser != null && "admin".equals(currentUser.getRole()); }
}
