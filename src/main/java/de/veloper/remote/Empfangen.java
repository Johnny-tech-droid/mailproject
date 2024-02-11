package de.veloper.remote;

import jakarta.mail.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;


/**
 * Diese Klasse repräsentiert ein Fenster zum Empfangen von E-Mails.
 */
public class Empfangen extends JFrame {
    private JTable tabelle;
    private DefaultTableModel modell;
    private final MeineAktionen antwortenAct;
    private final MeineAktionen weiterleitenAct;
    private final MeineAktionen homeAct;
    private final MeineAktionen syncAct;

    /**
     * Benutzername und Kennwort sollen nicht mehr fest im Code hinterlegt werden,
     * sondern von außen geladen werden. Durch eine Eingabe von TextFeld bzw. PasswortFeld
     * temp. gespeicherte Anmeldedaten werden für verschiedene Aufgaben der App verwendet.
     */
    private final String benutzername = Constants.getBENUTZERNAME();
    private final String passwort = Constants.getPASSWORT();

    /**
     * Ein WindowAdapter für das Empfangen von Nachrichten beim Öffnen des Fensters.
     */
    class MeinWindowAdapter extends WindowAdapter {
        @Override
        public void windowOpened(WindowEvent e) {
            nachrichtenEmpfangen();
        }
    }

    /**
     * Eine benutzerdefinierte Aktion für die Toolbar-Buttons.
     */
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
            if (e.getActionCommand().equals("home"))
                nachHause();
            if (e.getActionCommand().equals("syncMailbox"))
                synchronisieren();
        }
    }

    private void synchronisieren() {
        nachrichtenEmpfangen();

    }

    private void nachHause() {
        dispose();
    }

    /**
     * Konstruktor für das Empfangen-Fenster.
     */
    public Empfangen() {
        super();
        setTitle("E-Mail empfangen");
        setLayout(new BorderLayout());
        // neue Buttons, gleiches Muster wie bei Senden.java Actions
        antwortenAct = new MeineAktionen("Antworten",
                new ImageIcon("src/main/resources/icons/reply_icon_half.png"),
                "Antwortet auf die ausgewählte E-Mail",
                null,
                "antworten");
        weiterleitenAct = new MeineAktionen("Weiterleiten",
                new ImageIcon("src/main/resources/icons/forward_icon_half.png"),
                "Leitet die ausgewählte E-Mail weiter",
                null,
                "weiterleiten");
        homeAct = new MeineAktionen("Hauptmenü", new ImageIcon("src/main/resources/icons/home_icon.png"), "Zurück zum Hauptmenü", null, "home");
        syncAct = new MeineAktionen("Sync", new ImageIcon("src/main/resources/icons/syncbox_icon_half.png"), "Synchronisiert den Posteingang", null, "syncMailbox");
        add(symbolleiste(), BorderLayout.NORTH);
        setVisible(true);
        setSize(700, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new MeinWindowAdapter());
        tabelleErstellen();
        tabelleAktualisieren();
    }


    /**
     * Erstellt die Toolbar zusammen mit den Buttons.
     *
     * @return Die erstellte Toolbar.
     */
    private JToolBar symbolleiste() {
        JToolBar leiste = new JToolBar();
        // die Symbole über die Aktionen einbauen
        leiste.add(homeAct);
        leiste.add(antwortenAct);
        leiste.add(weiterleitenAct);

        leiste.add(syncAct);

        // die Leiste zurückgeben
        return leiste;
    }

    /**
     * Aktualisiert die Tabelle mit den empfangenen E-Mails.
     */
    private void tabelleAktualisieren() {
        Connection verbindung;
        ResultSet ergebnisMenge;
        String sender, betreff, inhalt, ID;
        modell.setRowCount(0);
        try {
            verbindung = MiniDBTools.oeffnenDB(Constants.getDB_URL());
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
            MiniDBTools.schliessenDB(Constants.getDB_URL());
        } catch (SQLException e) {
            e.printStackTrace();
            e.toString();
        }
    }

    /**
     * Erstellt die Tabelle für die Anzeige der empfangenen E-Mails.
     */
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

    /**
     * Empfängt neue Nachrichten und aktualisiert die Tabelle.
     */
    private void nachrichtenEmpfangen() {
        nachrichtenAbholen();
        tabelleAktualisieren();
    }

    /**
     * Holt neue Nachrichten vom Mail-Server.
     */
    private void nachrichtenAbholen() {
        // der (imap) Server
        String server = "outlook.office365.com";
        // die Eigenschaften setzen
        Properties eigenschaften = new Properties();

        // das Protokoll
        eigenschaften.put("mail.imap.auth", "true");
        // die Authentifizierung über TLS
        eigenschaften.put("mail.imap.starttls.enable", "true");
        // den Host
        eigenschaften.put("mail.imap.host", server);
        // den Port zum Empfangen
        eigenschaften.put("mail.imap.port", "993");

        // das Session-Objekt erstellen
        Session sitzung = Session.getDefaultInstance(eigenschaften);

        // das Store-Objekt über die Sitzung erzeugen
        try (Store store = sitzung.getStore("imaps")) {
            // und verbinden
            store.connect(server, benutzername, passwort);
            // ein Ordnerobjekt für den Posteingang erzeugen
            // und öffnen
            // dabei sind auch Änderungen zugelassen
            Folder posteingang = store.getFolder("INBOX");
            // und öffnen
            // dabei sind auch Änderungen zugelassen
            posteingang.open(Folder.READ_WRITE);

            // die Nachrichten beschaffen
            Message[] nachrichten = posteingang.getMessages();

            // gibt es neue Nachrichten?
            if (nachrichten.length != 0) {
                // dann die Anzahl zeigen
                JOptionPane.showMessageDialog(this, "Es gibt " + posteingang.getUnreadMessageCount() + " neue Nachrichten.");
                // jede Nachricht verarbeiten
                for (Message nachricht : nachrichten)
                    nachrichtVerarbeiten(nachricht);
            } else
                JOptionPane.showMessageDialog(this, "Es gibt keine neue Nachrichten.");

            // den Ordner schließen
            // durch das Argument true werden die Nachrichten gelöscht
            posteingang.close(true);
        } catch (Exception e) {
            e.printStackTrace();
            e.toString();
        }
    }

    /**
     * Verarbeitet eine empfangene Nachricht.
     *
     * @param nachricht Die empfangene Nachricht.
     */
    private void nachrichtVerarbeiten(Message nachricht) {
        try {
            // ist es einfacher Text?
            if (nachricht.isMimeType("text/plain")) {
                // den ersten Sender beschaffen
                String sender = nachricht.getFrom()[0].toString();
                // den Betreff
                String betreff = nachricht.getSubject();
                // den Inhalt
                String inhalt = nachricht.getContent().toString();
                // und die Nachricht speichern
                nachrichtSpeichern(sender, betreff, inhalt);
                // und zum Löschen markieren
                nachricht.setFlag(Flags.Flag.DELETED, true);
            }
            // sonst geben wir eine Meldung aus
            else
                JOptionPane.showMessageDialog(this,
                        "Der Typ der Nachricht "
                                + nachricht.getContentType()
                                + "kann nicht verarbeitet werden.");
        } catch (Exception e) {
            e.printStackTrace();
            e.toString();
        }
    }

    /**
     * Speichert eine empfangene Nachricht in der Datenbank.
     *
     * @param sender  Der Sender der Nachricht.
     * @param betreff Der Betreff der Nachricht.
     * @param inhalt  Der Inhalt der Nachricht.
     */
    private void nachrichtSpeichern(String sender, String betreff, String inhalt) {
        // für die Verbindung
        Connection verbindung;

        // die Datenbank öffnen
        verbindung = MiniDBTools.oeffnenDB(Constants.getDB_URL());
        try {
            // einen Eintrag in der Tabelle empfangen anlegen
            // über ein vorbereitetes Statement
            PreparedStatement prepState;
            prepState = verbindung.prepareStatement("insert into empfangen (sender, betreff, inhalt) values (?, ?, ?)");
            prepState.setString(1, sender);
            prepState.setString(2, betreff);
            prepState.setString(3, inhalt);
            // das Statement ausführen
            prepState.executeUpdate();
            verbindung.commit();

            // Verbindung schließen
            prepState.close();
            verbindung.close();
            // und die Datenbank schließen
            MiniDBTools.schliessenDB(Constants.getDB_URL());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }

    // AUFGABE 2 EINSENDEAUFGABE JAVA17D
    // antworten und weiterleiten Methoden sind abgewandelt von tabelleErstellen() für das Anzeigen
    // einer empfangenen Nachricht
    /**
     * Antwortet auf die ausgewählte Nachricht. Ruft ein neues Fenster für eine neue Nachricht auf,
     * füllt automatisch den Empfänger, den Betreff und den Inhalt mit den entsprechenden Werten der
     * ausgewählten Nachricht. Aktualisiert anschließend die Tabelle.
     */
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

    /**
     * Leitet die ausgewählte Nachricht weiter. Ruft ein neues Fenster für eine neue Nachricht auf,
     * füllt automatisch den Betreff und den Inhalt mit den entsprechenden Werten der
     * ausgewählten Nachricht. Aktualisiert anschließend die Tabelle.
     */
    private void weiterleiten() {
        int selectedRow = tabelle.getSelectedRow();
        if (selectedRow >= 0) {

            String betreff = "WG: " + tabelle.getModel().getValueAt(selectedRow, 2).toString();
            String orgInhalt = tabelle.getModel().getValueAt(selectedRow, 3).toString();
            String inhalt = "\n\n------- Text der ursprünglichen Nachricht --------\n" + orgInhalt;
            new NeueNachricht(this, true, betreff, inhalt);
            tabelleAktualisieren();
        } else {
            JOptionPane.showMessageDialog(this, "Eine Nachricht anklicken für weiterleiten.");
        }
    }
}
