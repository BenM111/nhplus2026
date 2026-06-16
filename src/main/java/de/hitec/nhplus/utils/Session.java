package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.User;

/**
 * Eine Utility-Klasse zur Verwaltung der aktuellen Benutzersitzung (Session).
 * Hält den global angemeldeten Benutzer im Speicher und bietet Hilfsmethoden
 * zur Abfrage von Benutzerrechten (z. B. Administrator-Prüfung) während der Laufzeit.
 *
 * @author Luca Bullwinkel
 */
public class Session {

    /**
     * Der aktuell im System angemeldete Benutzer.
     * Statisch, damit von überall in der Anwendung auf die Sitzungsdaten zugegriffen werden kann.
     */
    private static User currentUser;

    /**
     * Setzt den aktuell angemeldeten Benutzer für die laufende Sitzung.
     * Wird in der Regel nach erfolgreichem Login im Controller aufgerufen.
     *
     * @param user Das {@link User}-Objekt des angemeldeten Benutzers.
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Gibt den aktuell angemeldeten Benutzer der Sitzung zurück.
     *
     * @return Das aktuelle {@link User}-Objekt, oder {@code null}, wenn kein Benutzer eingeloggt ist.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Prüft, ob ein Benutzer angemeldet ist und ob dieser über Administrator-Rechte verfügt.
     *
     * @return {@code true}, wenn der aktuelle Benutzer existiert und die Rolle "admin" besitzt,
     * ansonsten {@code false}.
     */
    public static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
}