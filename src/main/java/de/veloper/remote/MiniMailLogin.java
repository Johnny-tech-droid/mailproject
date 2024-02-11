package de.veloper.remote;

import jakarta.mail.*;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;
/**
 * Diese Klasse repräsentiert das Login-Fenster der MiniMail-Anwendung.
 */

public class MiniMailLogin extends JDialog {
    // die Variablen für die Logindaten
    private JTextField benutzerFeld;
    private JPasswordField passwortFeld;
    // die eigenschaften für die Verbindung mit dem Server
    private Properties smtpProperties;

    /**
     * Konstruktor für das MiniMail-Login-Fenster.
     * Initialisiert die GUI und setzt die Benutzerdaten.
     */
    public MiniMailLogin() {
        initGui();
        Constants.setBENUTZERNAME(benutzerFeld.getText());
        Constants.setPASSWORT(new String(passwortFeld.getPassword()));

    }
    /**
     * Initialisiert die grafische Benutzeroberfläche des Login-Fensters.
     */
    private void initGui() {
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.CENTER);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            if (authenticate()) {
                new MiniMailStart("MiniMail Start");
                Constants.setBENUTZERNAME(benutzerFeld.getText());
                Constants.setPASSWORT(new String(passwortFeld.getPassword()));
                dispose();
            }
            else {
                System.err.println("FAILURE");
            }
        });
        add(loginBtn, BorderLayout.SOUTH);

        setTitle("MiniMail Login");
        setSize(300,150);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


    }
    /**
     * Erstellt das Panel für die Benutzereingabe.
     * @return Das erstellte Panel.
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));

        JLabel benutzerLabel = new JLabel("Benutzer:");
        benutzerFeld = new JTextField();

        JLabel passwortLabel = new JLabel("Passwort:");
        passwortFeld = new JPasswordField();

        inputPanel.add(benutzerLabel);
        inputPanel.add(benutzerFeld);
        inputPanel.add(passwortLabel);
        inputPanel.add(passwortFeld);

        return inputPanel;
    }
    /**
     * Authentifiziert den Benutzer über die SMTP-Verbindung.
     * @return True, wenn die Authentifizierung erfolgreich ist, sonst False.
     */
    public boolean authenticate() {
        setSmtpProperties();

        // Verbindung zum E-Mail-Server für die Authentifizierung der eingegebenen Nutzerdaten
        // angepasst für die Verbindung mit Outlook.com

        Session session = Session.getInstance(smtpProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(benutzerFeld.getText(), new String(passwortFeld.getPassword()));
            }
        });
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();
            return true;
        } catch (MessagingException e) {
            System.err.println("ERROR");
            return false;
        }


    }
    /**
     * Setzt die SMTP-Verbindungseigenschaften.
     */
    private void setSmtpProperties() {

        String smtpServer = Constants.getSmtpServer();
        String smtpPort = Constants.getSmtpPort();
        smtpProperties = createProperties(smtpServer, smtpPort, "mail.smtp");
    }
    /**
     * Erstellt die Eigenschaften für die Verbindung.

     * @param host Hostname des Servers.
     * @param port Port des Servers.
     * @param protocolPrefix Präfix für das Protokoll.
     * @return Die erstellten Eigenschaften.
     */
    private Properties createProperties(String host, String port, String protocolPrefix) {
        Properties properties = new Properties();
        properties.put(protocolPrefix + ".auth", true);
        properties.put(protocolPrefix + ".starttls.enable", true);
        properties.put(protocolPrefix + ".host", host);
        properties.put(protocolPrefix + ".port", port);
        return properties;
    }
}
