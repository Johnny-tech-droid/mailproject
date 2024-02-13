package de.veloper.remote;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.kordamp.ikonli.material2.Material2OutlinedAL.CANCEL;
import static org.kordamp.ikonli.material2.Material2OutlinedMZ.SEND;

public class NeueNachricht extends JDialog {
    // die Eingabefelder
    private JTextField empfaenger, betreff;
    private JTextArea inhalt;
    // die Variablen als Platzhalter der Logindaten
    private final String benutzername = Constants.getBENUTZERNAME();
    private final String passwort = Constants.getPASSWORT();

    // die innere Klasse für den ActionListener
    class NeuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            switch (e.getActionCommand()) {
                case "senden" -> senden();
                case "abbrechen" -> dispose();
            }
        }
    }


    // ------ KONSTRUKTOR ------ //
    // für allgemeine neue Nachricht verfassen
    public NeueNachricht(JFrame parent, boolean modal) {
        super(parent, modal);
        setTitle("Neue Nachricht");
        initGui();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    // für Nachricht weiterleiten
    public NeueNachricht(JFrame parent, boolean modal, String betreff, String inhalt) {
        super(parent, modal);
        setTitle("Weiterleiten");
        initGui(betreff, null, inhalt);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    // für Nachricht beantworten
    public NeueNachricht(JFrame parent, boolean modal, String empfaenger, String betreff, String inhalt) {
        super(parent, modal);
        setTitle("Antwort");
        initGui(betreff, empfaenger, inhalt);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    // Methode zum Versenden der Nachricht über den E-Mail-Server
    // Verbindung zur DB mit nachrichtSpeichern() Methode
    private void senden() {
        Session sitzung = verbindungHerstellen();
        nachrichtVerschicken(sitzung);
        nachrichtSpeichern();
    }

    // --------- DATABASE RELATED ------ //
    private void nachrichtSpeichern() {
        Connection verbindung;
        verbindung = MiniDBTools.oeffnenDB(Constants.getDB_URL());
        try {
            //einen Eintrag in der Tabelle gesendet anlegen
            //über ein vorbereitetes Statement
            PreparedStatement prepState;
            prepState = verbindung.prepareStatement("insert into gesendet (empfaenger, betreff, inhalt) values (?, ?, ?)");
            prepState.setString(1, empfaenger.getText());
            prepState.setString(2, betreff.getText());
            prepState.setString(3, inhalt.getText());
            //das Statement ausführen
            prepState.executeUpdate();
            verbindung.commit();

            //Verbindung schließen
            prepState.close();
            verbindung.close();
            //und die Datenbank schließen
            MiniDBTools.schliessenDB(Constants.getDB_URL());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Problem: \n" + e);
        }
    }

    // ------- EMAIL SERVER RELATED -------- //
    private void nachrichtVerschicken(Session sitzung) {
        String absender = benutzername;
        try {
            MimeMessage nachricht = new MimeMessage(sitzung);
            nachricht.setFrom(absender);
            nachricht.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger.getText()));
            nachricht.setSubject(betreff.getText());
            nachricht.setText(inhalt.getText());
            Transport.send(nachricht);
            JOptionPane.showMessageDialog(this, "Die Nachricht wurde verschickt");
            dispose();
        } catch (RuntimeException | MessagingException e) {
            throw new RuntimeException(e);
        }

    }


    private Session verbindungHerstellen() {

        //der Server
        String server = Constants.getSmtpServer();
        //die Eigenschaften setzen
        Properties eigenschaften = new Properties();
        //die Authentifizierung über TLS
        eigenschaften.put("mail.smtp.auth", "true");
        eigenschaften.put("mail.smtp.starttls.enable", "true");
        //der Server
        eigenschaften.put("mail.smtp.host", server);
        //der Port zum Versenden
        eigenschaften.put("mail.smtp.port", "587");
        // das Session-Objekt erstellen
        return Session.getInstance(eigenschaften, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(benutzername, passwort);
            }
        });
    }

    // ------- VIEW RELATED ------ //
    private void initGui() {
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel oben = createInputPanel(null, null);
        add(oben, BorderLayout.NORTH);
        // Text area for email content
        JScrollPane scroll = createContentTextArea(null);
        add(scroll);
        // Panel for buttons
        JPanel unten = createButtonPanel();
        add(unten, BorderLayout.SOUTH);
        // Set frame properties
        setSize(600, 300);
        setVisible(true);
    }

    private void initGui(String betreffTxt, String empfaengerTxt, String inhaltTxt) {
        setLayout(new BorderLayout());
        // Panel for input fields
        JPanel oben = createInputPanel(betreffTxt, empfaengerTxt);
        add(oben, BorderLayout.NORTH);
        // Text area for email content
        JScrollPane scroll = createContentTextArea(inhaltTxt);
        add(scroll);
        // Panel for buttons
        JPanel unten = createButtonPanel();
        add(unten, BorderLayout.SOUTH);
        // Set frame properties
        setSize(600, 300);
        setVisible(true);
    }

    private JPanel createInputPanel(String betreffTxtFeld, String empfaengerTxtFeld) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        panel.add(new JLabel("Empfänger:"));
        empfaenger = new JTextField();
        empfaenger.setText(empfaengerTxtFeld);
        panel.add(empfaenger);

        panel.add(new JLabel("Betreff:"));
        betreff = new JTextField();
        betreff.setText(betreffTxtFeld);
        panel.add(betreff);

        return panel;
    }


    private JScrollPane createContentTextArea(String content) {
        inhalt = new JTextArea();
        inhalt.setLineWrap(true);
        inhalt.setWrapStyleWord(true);
        inhalt.setText(content);

        JScrollPane scroll = new JScrollPane(inhalt);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scroll;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        NeuListener listener = new NeuListener();
        // Buttons
        JButton sendEmailBtn = createButton(SEND, "senden", listener, "E-Mail absenden");
        JButton abbrechen = createButton(CANCEL, "abbrechen", listener, "Abbrechen und Fenster schließen");
        panel.add(sendEmailBtn);
        panel.add(abbrechen);

        return panel;
    }

    private JButton createButton(Ikon iconEnum, String actionCommand, NeuListener actionListener, String toolTipText) {
        JButton tempButton = new JButton();
        FontIcon tempIcon = new FontIcon();
        tempIcon.setIconSize(25);
        tempIcon.setIkon(iconEnum);
        tempButton.setIcon(tempIcon);
        tempButton.setActionCommand(actionCommand);
        tempButton.addActionListener(actionListener);
        tempButton.setToolTipText(toolTipText);
        return tempButton;
    }

}
