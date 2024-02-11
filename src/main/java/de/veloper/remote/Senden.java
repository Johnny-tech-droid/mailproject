package de.veloper.remote;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;

public class Senden extends JFrame {
    private MeineAktionen sendenAct;
    private JTable tabelle;
    private DefaultTableModel modell;


    class MeineAktionen extends AbstractAction {
        public MeineAktionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
            super(text, icon);

            putValue(SHORT_DESCRIPTION, beschreibung);
            putValue(ACCELERATOR_KEY, shortcut);
            putValue(ACTION_COMMAND_KEY, actionText);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand().equals("senden"))
                senden();

        }
    }

    private void senden() {
        //den Dialog für eine neue Nachricht modal anzeigen
        new NeueNachricht(this, true);
        //nach dem Versenden lassen wir die Anzeige aktualisieren
        tabelleAktualisieren();
    }



    public Senden() {
        super();
        setTitle("E-Mail senden");
        setLayout(new BorderLayout());
        sendenAct = new MeineAktionen("Neue E-Mail",
                new ImageIcon("src/main/resources/icons/create_icon_half.png"),
                "Erstellt eine neue E-Mail",
                null,
                "senden");

        add(symbolleiste(), BorderLayout.NORTH);
        setVisible(true);
        setSize(700,300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tabelleErstellen();
        tabelleAktualisieren();
    }


    private void tabelleErstellen() {
        //für die Spaltenbezeichner
        String[] spaltenNamen = {"ID", "Empfänger", "Betreff", "Text"};

        //ein neues Standardmodell erstellen
        modell = new DefaultTableModel();
        //die Spaltennamen setzen
        modell.setColumnIdentifiers(spaltenNamen);
        //die Tabelle erzeugen
        tabelle = new JTable();
        //und mit dem Modell verbinden
        tabelle.setModel(modell);
        //wir haben keinen Editor, können die Tabelle also nicht bearbeiten
        tabelle.setDefaultEditor(Object.class, null);
        //es sollen immer alle Spalten angepasst werden
        tabelle.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //und die volle Größe genutzt werden
        tabelle.setFillsViewportHeight(true);
        //die Tabelle setzen wir in ein Scrollpane
        JScrollPane scroll = new JScrollPane(tabelle);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll);

        //einen Maus-Listener ergänzen
        tabelle.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //war es ein Doppelklick?
                if (e.getClickCount() == 2) {
                    //die Zeile beschaffen
                    int zeile = tabelle.getSelectedRow();
                    //die Daten beschaffen
                    String empfaenger, betreff, inhalt, ID;
                    ID = tabelle.getModel().getValueAt(zeile, 0).toString();
                    empfaenger = tabelle.getModel().getValueAt(zeile, 1).toString();
                    betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
                    inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
                    //und anzeigen
                    //übergeben wird der Frame der äußeren Klasse
                    new Anzeige(Senden.this, true, ID, empfaenger, betreff, inhalt);
                }
            }
        });
    }
    private void tabelleAktualisieren() {
        //für den Datenbankzugriff
        Connection verbindung;
        ResultSet ergebnisMenge;

        //für die Spalten
        String empfaenger, betreff, inhalt, ID;
        //die Inhalte löschen
        modell.setRowCount(0);

        try{
            //Verbindung herstellen und Ergebnismenge beschaffen
            verbindung = MiniDBTools.oeffnenDB(Constants.getDB_URL());
            ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM gesendet");
            //die Einträge in die Tabelle schreiben
            while (ergebnisMenge.next()) {
                ID = ergebnisMenge.getString("iNummer");
                empfaenger = ergebnisMenge.getString("empfaenger");
                betreff = ergebnisMenge.getString("betreff");
                //den Inhalt vom CLOB beschaffen und in einen String umbauen
                Clob clob;
                clob = ergebnisMenge.getClob("inhalt");
                inhalt = clob.getSubString(1, (int) clob.length());

                //die Zeile zum Modell hinzufügen
                //dazu benutzen wir ein Array vom Typ Object
                modell.addRow(new Object[] {ID, empfaenger, betreff, inhalt} );
            }
            //die Verbindungen wieder schließen und trennen
            ergebnisMenge.close();
            verbindung.close();
            MiniDBTools.schliessenDB(Constants.getDB_URL());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
        }
    }



    private JToolBar symbolleiste() {
        JToolBar leiste = new JToolBar();
        //die Symbole über die Aktionen einbauen
        leiste.add(sendenAct);
        //die Leiste zurückgeben
        return (leiste);
    }
}
