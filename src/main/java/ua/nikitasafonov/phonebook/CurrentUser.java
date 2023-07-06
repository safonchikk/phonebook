package ua.nikitasafonov.phonebook;

import ua.nikitasafonov.phonebook.model.AppUser;

public class CurrentUser {
    private static AppUser currentUser;

    public static AppUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(AppUser user) {
        currentUser = user;
    }
}
