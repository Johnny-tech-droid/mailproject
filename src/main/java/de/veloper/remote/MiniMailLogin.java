package de.veloper.remote;

import jakarta.mail.*;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

import static org.kordamp.ikonli.material2.Material2OutlinedAL.LOGIN;


/**
 * Diese Klasse repräsentiert das Login-Fenster der MiniMail-Anwendung.
 */

public class MiniMailLogin extends JDialog {
    // die Variablen für die Logindaten
    private JTextField benutzerFeld;
    private JButton loginBtn;
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

        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);
        borderLayout.setHgap(10);
        borderLayout.setVgap(10);
        JPanel inputPanel = createInputPanel();

        add(inputPanel, BorderLayout.CENTER);
        JPanel loginBtnPanel = createLoginButton();
        add(loginBtnPanel, BorderLayout.SOUTH);
        setTitle("MiniMail Login");
        setSize(380, 150);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        setResizable(false);

    }

    /**
     * Erstellt das Panel für die Benutzereingabe.
     *
     * @return Das erstellte Panel.
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
//        flowLayout.setHgap(10);

        JLabel benutzerLabel = new JLabel("Benutzer:");
        benutzerFeld = new JTextField();
        benutzerFeld.setColumns(20);
        benutzerFeld.setLayout(flowLayout);

        JLabel passwortLabel = new JLabel("Passwort:");
        passwortFeld = new JPasswordField();
        passwortFeld.setColumns(20);

        inputPanel.add(benutzerLabel);
        inputPanel.add(benutzerFeld);
        inputPanel.add(passwortLabel);
        inputPanel.add(passwortFeld);

        return inputPanel;
    }

    /**
     * Authentifiziert den Benutzer über die SMTP-Verbindung.
     *
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
     *
     * @param host           Hostname des Servers.
     * @param port           Port des Servers.
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

    private JButton createButton(Ikon iconEnum, String tooltiptext) {
        JButton tempButton = new JButton();
        FontIcon tempIcon = new FontIcon();
        tempIcon.setIkon(iconEnum);
        tempIcon.setIconSize(25);
        tempButton.setIcon(tempIcon);

        tempButton.setToolTipText(tooltiptext);
        return tempButton;
    }

    private JPanel createLoginButton() {
        JPanel tempPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(0,3);
        tempPanel.setLayout(gridLayout);
        loginBtn = createButton(LOGIN, "Jetzt einloggen");
        loginBtn.addActionListener(e -> checkForAuthentication());
        tempPanel.add(new JLabel());
//        gridLayout.setHgap(10);
//        gridLayout.setVgap(10);
        tempPanel.add(loginBtn);

        return tempPanel;
    }

    private void checkForAuthentication() {
        if (authenticate()) {
            new MiniMailStart("MiniMail Start");
            Constants.setBENUTZERNAME(benutzerFeld.getText());
            Constants.setPASSWORT(new String(passwortFeld.getPassword()));
            dispose();
        } else {
            System.err.println("FAILURE");
        }
    }
}
