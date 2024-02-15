package de.veloper.remote;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.kordamp.ikonli.materialdesign2.MaterialDesignE.EMAIL_RECEIVE_OUTLINE;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignE.EMAIL_SEND_OUTLINE;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignL.LOGOUT;

public class MiniMailStart extends JFrame {
    class MeinListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            switch(e.getActionCommand()) {
                case "senden" -> senden();
                case "empfangen" -> empfangen();
                case "ende" -> beenden();

            }
        }
    }

      public MiniMailStart(String title) throws HeadlessException {
        super(title);
        JPanel guiPanel = createButtonPanel();
        add(guiPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(472,78);
        setResizable(false);
        setLocationRelativeTo(null);

    }
    private JPanel createButtonPanel() {
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new GridLayout(0,3));
        MeinListener listener = new MeinListener();

        JButton postausgang = createButton(EMAIL_SEND_OUTLINE,
                "senden", listener,
                "Öffnet den Posteingang und synchronisiert diesen.",
                "Gesendete E-Mails");
        JButton posteingang = createButton(EMAIL_RECEIVE_OUTLINE,
                "empfangen", listener,
                "Öffnet die gesendeten E-Mails.",
                "Empfangene E-Mails");
        JButton ausloggen = createButton(LOGOUT,
                "ende", listener,
                "User ausloggen und Programm beenden.",
                "Programm beenden");

        tempPanel.add(posteingang);
        tempPanel.add(postausgang);
        tempPanel.add(ausloggen);

        pack();
        return tempPanel;
    }


    private JButton createButton(Ikon iconEnum, String actionCommand, ActionListener actionListener, String toolTipText, String text) {
        JButton tempButton = new JButton();
        FontIcon fontIcon = createFontIcon(iconEnum);
        tempButton.setIcon(fontIcon);
        tempButton.setActionCommand(actionCommand);
        tempButton.addActionListener(actionListener);
        tempButton.setToolTipText(toolTipText);
        tempButton.setText(text);
        tempButton.setBorderPainted(false);
        return tempButton;
    }

    private FontIcon createFontIcon(Ikon name) {
        FontIcon tempIcon = new FontIcon();
        tempIcon.setIkon(name);
        tempIcon.setIconSize(25);

        return tempIcon;
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
