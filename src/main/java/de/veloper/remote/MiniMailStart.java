package de.veloper.remote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MiniMailStart extends JFrame {
    class MeinListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("senden"))
                senden();
            if (e.getActionCommand().equals("empfangen"))
                empfangen();
            if (e.getActionCommand().equals("ende"))
                beenden();
        }
    }
    public MiniMailStart(String title) throws HeadlessException {
        super(title);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton liste = new JButton("Senden");
        JButton einzel = new JButton("Empfangen");
        JButton beenden = new JButton("Beenden");

        MeinListener listener = new MeinListener();

        liste.setActionCommand("senden");
        einzel.setActionCommand("empfangen");
        beenden.setActionCommand("ende");

        liste.addActionListener(listener);
        einzel.addActionListener(listener);
        beenden.addActionListener(listener);

        add(liste);
        add(einzel);
        add(beenden);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }
    private void senden() {
        new Senden();
    }
    private void empfangen() {
        new Empfangen();
    }
    private void beenden() {
        System.exit(0);
    }
}
