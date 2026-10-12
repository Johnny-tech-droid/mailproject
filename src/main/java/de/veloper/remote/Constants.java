package de.veloper.remote;

public class Constants {
    // Die Konstanten für die Anmeldedaten
    private static String BENUTZERNAME;
    private static String PASSWORT;
    private static final String SMTP_SERVER = "smtp-mail.outlook.com";

    private static String SMTP_PORT = "587";


    // Die URL der Datenbank für die Verbindung mit der Datenbank
    private static final String DB_URL = "jdbc:derby:mailprojectDB";

    // ----- GETTER UND SETTER ------- //
    public static String getDB_URL() {
        return DB_URL;
    }

    public static String getBENUTZERNAME() {
        return BENUTZERNAME;
    }

    public static void setBENUTZERNAME(String BENUTZERNAME) {
        Constants.BENUTZERNAME = BENUTZERNAME;
    }

    public static String getPASSWORT() {
        return PASSWORT;
    }

    public static void setPASSWORT(String PASSWORT) {
        Constants.PASSWORT = PASSWORT;
    }

    public static String getSmtpServer() {
        return SMTP_SERVER;
    }

    public static String getSmtpPort() {
        return SMTP_PORT;
    }
}
