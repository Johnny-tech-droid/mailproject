package de.veloper.remote;

import jakarta.mail.*;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class MiniMailLogin extends JDialog {
    private JTextField benutzerFeld;
    private JPasswordField passwortFeld;
    private Properties smtpProperties;
    private final String SMTP_SERVER = "smtp-mail.outlook.com";
    private final String SMTP_PORT = "587";
    public MiniMailLogin() {
        initGui();
        Constants.setBenutzername(benutzerFeld.getText());
        Constants.setPasswort(new String(passwortFeld.getPassword()));

    }
    private void initGui() {
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.CENTER);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            if (authenticate()) {
                new MiniMailStart("MiniMail Start");
                Constants.setBenutzername(benutzerFeld.getText());
                Constants.setPasswort(new String(passwortFeld.getPassword()));
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

    public boolean authenticate() {
        setSmtpProperties();

        // smtp properties

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
    private void setSmtpProperties() {

        smtpProperties = createProperties(true, true, SMTP_SERVER, SMTP_PORT, "mail.smtp");
    }
    private Properties createProperties(boolean auth, boolean tls, String host, String port, String protocolPrefix) {
        Properties properties = new Properties();
        properties.put(protocolPrefix + ".auth", auth);
        properties.put(protocolPrefix + ".starttls.enable", tls);
        properties.put(protocolPrefix + ".host", host);
        properties.put(protocolPrefix + ".port", port);
        return properties;
    }
}
