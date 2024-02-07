package de.veloper.remote;

public class Constants {
    private static String benutzername;
    private static String passwort;

    public static String getBenutzername() {
        return benutzername;
    }

    public static void setBenutzername(String benutzername) {
        Constants.benutzername = benutzername;
    }

    public static String getPasswort() {
        return passwort;
    }

    public static void setPasswort(String passwort) {
        Constants.passwort = passwort;
    }
}
