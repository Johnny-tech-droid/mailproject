package de.veloper.remote;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class NeueNachricht extends JDialog{
    private JTextField empfaenger, betreff;
    private String benutzername = Constants.getBenutzername();
    private String passwort = Constants.getPasswort();
    private JTextArea inhalt;
    private JButton ok, abbrechen;
    class NeuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("senden"))
                senden();
            if (e.getActionCommand().equals("abbrechen"))
                dispose();
        }
    }

    private void senden() {
        Session sitzung = verbindungHerstellen();
        nachrichtVerschicken(sitzung);
        nachrichtSpeichern();

    }

    private void nachrichtSpeichern() {
        Connection verbindung;
        verbindung = MiniDBTools.oeffnenDB("jdbc:derby:/Users/developer/Developer/Database/mailprojectDB");
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
            MiniDBTools.schliessenDB("jdbc:derby:/Users/developer/Developer/Database/mailprojectDB");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Problem: \n" + e);
        }
    }

    private void nachrichtVerschicken(Session sitzung) {
        String absender = benutzername;

    }

    private Session verbindungHerstellen() {

        //der Server
        String server = "smtp-mail.outlook.com";

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
        Session sitzung = Session.getInstance(eigenschaften, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(benutzername, passwort);
            }
        });

        return sitzung;
    }

    public NeueNachricht(JFrame parent, boolean modal) {
        super(parent, modal);
        setTitle("Neue Nachricht");
        initGui();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    public NeueNachricht(JFrame parent, boolean modal, String empfaenger, String betreff, String inhalt) {
        super(parent, modal);
        setTitle("Antwort");
        initGui(betreff, empfaenger, inhalt);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initGui() {
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel oben = createInputPanel(null,null);
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
        JPanel oben = createInputPanel(betreffTxt,empfaengerTxt);
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

        // Buttons
        ok = new JButton("Senden");
        ok.setActionCommand("senden");
        abbrechen = new JButton("Abbrechen");
        abbrechen.setActionCommand("abbrechen");

        // Action listener
        NeuListener listener = new NeuListener();
        ok.addActionListener(listener);
        abbrechen.addActionListener(listener);

        panel.add(ok);
        panel.add(abbrechen);

        return panel;
    }

}
