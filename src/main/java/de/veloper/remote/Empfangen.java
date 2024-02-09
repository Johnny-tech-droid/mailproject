package de.veloper.remote;

import jakarta.mail.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;

public class Empfangen extends JFrame {
    private JTable tabelle;
    private DefaultTableModel modell;
    private final MeineAktionen antwortenAct;
    private final MeineAktionen weiterleitenAct;
    // ---- Einsendeaufgabe JAVA17D-XX01-N01 -----
    // TODO: AUFGABE 1

    /**
     * Benutzername und Kennwort soll nicht mehr fest im Code hinterlegt werden,
     * sondern von außen geladen werden
     * Durch eine Eingabe von TextFeld bzw. PasswortFeld
     * temp. gespeicherte Anmeldedaten werden für versch. Aufgaben der App verwendet
     */
    private final String benutzername = Constants.getBenutzername();
    private final String passwort = Constants.getPasswort();
    class MeinWindowAdapter extends WindowAdapter{
        @Override
        public void windowOpened(WindowEvent e) {
            nachrichtenEmpfangen();
        }
    }
    class MeineAktionen extends AbstractAction {
        public MeineAktionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
            super(text, icon);

            putValue(SHORT_DESCRIPTION, beschreibung);
            putValue(ACCELERATOR_KEY, shortcut);
            putValue(ACTION_COMMAND_KEY, actionText);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand().equals("antworten"))
                antworten();
            if (e.getActionCommand().equals("weiterleiten"))
                weiterleiten();
        }
    }

    // Konstruktor
    public Empfangen() {
        super();
        setTitle("E-Mail empfangen");
        setLayout(new BorderLayout());
        antwortenAct = new MeineAktionen("Antworten",
                new ImageIcon("src/main/resources/icons/antworten.png"),
                "Antwortet auf die ausgewaehlte E-Mail",
                null,
                "antworten");
        weiterleitenAct = new MeineAktionen("Weiterleiten",
                new ImageIcon("src/main/resources/icons/weiterleiten.png"),
                "Leitet die ausgewaehlte E-Mail weiter",
                null,
                "weiterleiten");
        add(symbolleiste(), BorderLayout.NORTH);
        setVisible(true);
        setSize(700,300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new MeinWindowAdapter());
        tabelleErstellen();
        tabelleAktualisieren();

    }
    // Erstellt die Toolbar zsm mit den Buttons
    private JToolBar symbolleiste() {
        JToolBar leiste = new JToolBar();
        //die Symbole über die Aktionen einbauen

        leiste.add(antwortenAct);
        leiste.add(weiterleitenAct);

        //die Leiste zurückgeben
        return (leiste);
    }

    private void tabelleAktualisieren() {
        Connection verbindung;
        ResultSet ergebnisMenge;
        String sender, betreff, inhalt, ID;
        modell.setRowCount(0);
        try{
            verbindung = MiniDBTools.oeffnenDB("jdbc:derby:/Users/developer/Developer/Database/mailprojectDB");
            ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM empfangen");
            while (ergebnisMenge.next()) {
                ID = ergebnisMenge.getString("iNummer");
                sender = ergebnisMenge.getString("sender");
                betreff = ergebnisMenge.getString("betreff");
                Clob clob;
                clob = ergebnisMenge.getClob("inhalt");
                inhalt = clob.getSubString(1, (int) clob.length());
                modell.addRow(new Object[]{
                        ID, sender, betreff, inhalt
                });
            }
            ergebnisMenge.close();
            verbindung.close();
            MiniDBTools.schliessenDB("jdbc:derby:/Users/developer/Developer/Database/mailprojectDB");
        } catch (SQLException e) {
            e.printStackTrace();
            e.toString();
        }


    }

    private void tabelleErstellen() {
        String[] spaltenNamen = {"ID", "Sender", "Betreff", "Text"};
        modell = new DefaultTableModel();
        modell.setColumnIdentifiers(spaltenNamen);
        tabelle = new JTable();
        tabelle.setModel(modell);
        tabelle.setDefaultEditor(Object.class, null);
        tabelle.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabelle.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(tabelle);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll);
        tabelle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int zeile = tabelle.getSelectedRow();
                    String sender, betreff, inhalt, ID;
                    ID = tabelle.getModel().getValueAt(zeile, 0).toString();
                    sender = tabelle.getModel().getValueAt(zeile, 1).toString();
                    betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
                    inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
                    new Anzeige(Empfangen.this, true, ID, sender, betreff, inhalt);
                }
            }
        });
    }

    private void nachrichtenEmpfangen() {
        nachrichtenAbholen();
        tabelleAktualisieren();
    }
    private void nachrichtenAbholen() {


        // der (imap) Server
        String server = "outlook.office365.com";
        // die Eigenschaften setzen
        Properties eigenschaften = new Properties();

        //das Protokoll
        eigenschaften.put("mail.imap.auth", "true");
        //die Authentifizierung über TLS
        eigenschaften.put("mail.imap.starttls.enable", "true");
        //den Host
        eigenschaften.put("mail.imap.host", server);
        //den Port zum Empfangen
        eigenschaften.put("mail.imap.port", "993");


        //das Session-Objekt erstellen
        Session sitzung = Session.getDefaultInstance(eigenschaften);

        //das Store-Objekt über die Sitzung erzeugen
        try (Store store = sitzung.getStore("imaps")){
            //und verbinden
            store.connect(server, benutzername, passwort);
            //ein Ordnerobjekt für den Posteingang erzeugen     
            Folder posteingang  = store.getFolder("INBOX");
            //und öffnen
            //dabei sind auch Änderungen zugelassen
            posteingang.open(Folder.READ_WRITE);

            //die Nachrichten beschaffen
            Message[] nachrichten = posteingang.getMessages();

            //gibt es neue Nachrichten?
            if (nachrichten.length != 0) {
                //dann die Anzahl zeigen
                JOptionPane.showMessageDialog(this, "Es gibt "+ posteingang.getUnreadMessageCount() + " neue Nachrichten.");
                //jede Nachricht verarbeiten
                for(Message nachricht : nachrichten)
                    nachrichtVerarbeiten(nachricht);
            }
            else
                JOptionPane.showMessageDialog(this, "Es gibt keine neue Nachrichten.");

            //den Ordner schließen
            //durch das Argument true werden die Nachrichten gelöscht
            posteingang.close(true);
        }
        catch (Exception e) {
            e.printStackTrace();
            e.toString();
        }
    }

    private void nachrichtVerarbeiten(Message nachricht) {
        try {
            //ist es einfacher Text?
            if (nachricht.isMimeType("text/plain")) {
                //den ersten Sender beschaffen
                String sender = nachricht.getFrom()[0].toString();
                //den Betreff
                String betreff = nachricht.getSubject();
                //den Inhalt
                String inhalt = nachricht.getContent().toString();
                //und die Nachricht speichern
                nachrichtSpeichern(sender, betreff, inhalt);
                //und zum Löschen markieren
                nachricht.setFlag(Flags.Flag.DELETED, true);
            }
            //sonst geben wir eine Meldung aus
            else
                JOptionPane.showMessageDialog(this,
                        "Der Typ der Nachricht "
                        + nachricht.getContentType()
                        + "kann nicht verarbeitet werden.");
        }
        catch (Exception e) {
            e.printStackTrace();
            e.toString();
        }
        
        }

    private void nachrichtSpeichern(String sender, String betreff, String inhalt) {
        //für die Verbindung
        Connection verbindung;

        //die Datenbank öffnen
        verbindung=MiniDBTools.oeffnenDB("jdbc:derby:/Users/developer/Developer/Database/mailprojectDB");
        try {
            //einen Eintrag in der Tabelle empfangen anlegen
            //über ein vorbereitetes Statement
            PreparedStatement prepState;
            prepState = verbindung.prepareStatement("insert into empfangen (sender, betreff, inhalt) values (?, ?, ?)");
            prepState.setString(1, sender);
            prepState.setString(2, betreff);
            prepState.setString(3, inhalt);
            //das Statement ausführen
            prepState.executeUpdate();
            verbindung.commit();

            //Verbindung schließen
            prepState.close();
            verbindung.close();
            //und die Datenbank schließen
            MiniDBTools.schliessenDB("jdbc:derby:/Users/developer/Developer/Database/mailprojectDB");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }
    private void antworten() {
        int selectedRow = tabelle.getSelectedRow();
        if (selectedRow >= 0) {
            String empfaenger = tabelle.getModel().getValueAt(selectedRow, 1).toString();
            String betreff = "AW: " + tabelle.getModel().getValueAt(selectedRow, 2).toString();
            String orgInhalt = tabelle.getModel().getValueAt(selectedRow, 3).toString();
            String inhalt = "\n\n------- Text der ursprünglichen Nachricht --------\n" + orgInhalt;
            new NeueNachricht(this, true, empfaenger, betreff, inhalt);
            tabelleAktualisieren();
        } else {
            JOptionPane.showMessageDialog(this, "Eine Nachricht anklicken um zu antworten!");
        }
    }
    private void weiterleiten() {
        int selectedRow = tabelle.getSelectedRow();
        if (selectedRow >= 0) {
            String empfaenger = null;
            String betreff = "WG: " + tabelle.getModel().getValueAt(selectedRow, 2).toString();
            String orgInhalt = tabelle.getModel().getValueAt(selectedRow, 3).toString();
            String inhalt = "\n\n------- Text der ursprünglichen Nachricht --------\n" + orgInhalt;
            new NeueNachricht(this, true, empfaenger, betreff, inhalt);
            tabelleAktualisieren();
        } else {
            JOptionPane.showMessageDialog(this, "Eine Nachricht anklicken für weiterleiten.");
        }
    }
}
