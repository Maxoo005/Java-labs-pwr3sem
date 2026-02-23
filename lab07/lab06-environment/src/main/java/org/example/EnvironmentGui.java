package org.example;

import interfaces.IRiverSection;
import java.rmi.RemoteException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class EnvironmentGui extends JFrame {

    private final EnvironmentState state;
    private final JPanel riversPanel;
    private final Map<String, RiverPanel> riverUiMap = new HashMap<>();

    public EnvironmentGui(EnvironmentState state) {
        this.state = state;

        // Ustawienia Okna
        setTitle("Symulator Pogody - Środowisko");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        // Nagłówek (Globalne sterowanie)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185)); // Niebieski

        JLabel titleLabel = new JLabel("KONTROLA POGODY", JLabel.LEFT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(15, 20, 15, 10));

        // Panel globalnego deszczu
        JPanel globalRainPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        globalRainPanel.setOpaque(false);

        JTextField txtGlobalRain = new JTextField("0", 4);
        JButton btnRainAll = new JButton(" Deszcz wszędzie");
        btnRainAll.setBackground(Color.WHITE);
        btnRainAll.setForeground(new Color(41, 128, 185));
        btnRainAll.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnRainAll.addActionListener(e -> setRainEverywhere(txtGlobalRain.getText()));

        JLabel lblGlobal = new JLabel("Oberwanie chmury (m3/s): ");
        lblGlobal.setForeground(Color.WHITE);

        globalRainPanel.add(lblGlobal);
        globalRainPanel.add(txtGlobalRain);
        globalRainPanel.add(btnRainAll);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(globalRainPanel, BorderLayout.EAST);

        // Lista Rzek
        riversPanel = new JPanel();
        riversPanel.setLayout(new BoxLayout(riversPanel, BoxLayout.Y_AXIS));
        riversPanel.setBackground(new Color(236, 240, 241));

        JScrollPane scrollPane = new JScrollPane(riversPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Timer (Sprawdza czy pojawiły się nowe rzeki)
        Timer timer = new Timer(1000, e -> refreshList());
        timer.start();

        setVisible(true);
    }

    private void refreshList() {
        for (RiverInfo river : state.getRivers()) {
            if (!riverUiMap.containsKey(river.name())) {
                RiverPanel panel = new RiverPanel(river);
                riverUiMap.put(river.name(), panel);
                riversPanel.add(panel);
                riversPanel.add(Box.createVerticalStrut(10));
                riversPanel.revalidate();
            }
        }
        repaint();
    }

    private void setRainEverywhere(String valueStr) {
        try {
            int val = Integer.parseInt(valueStr);

            new Thread(() -> {
                for (RiverInfo r : state.getRivers()) {
                    try {
                        r.stub().setRainfall(val);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            JOptionPane.showMessageDialog(this, "Ustawiono deszcz " + val + " dla wszystkich rzek.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Błąd liczby!", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Panel pojedynczej rzeki
    private class RiverPanel extends JPanel {
        private final RiverInfo river;
        private final JTextField txtRain;
        private final JLabel lblStatus;

        public RiverPanel(RiverInfo river) {
            this.river = river;

            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(5, 10, 5, 10),
                    BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                            " Rzeka: " + river.name(),
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            new Font("Segoe UI", Font.BOLD, 14),
                            new Color(44, 62, 80))));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            // Lewa strona - ikona/opis
            JLabel lblIcon = new JLabel("💧");
            lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            lblIcon.setBorder(new EmptyBorder(0, 10, 0, 0));

            // Środek - Sterowanie
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            controlPanel.setBackground(Color.WHITE);

            JLabel lblSet = new JLabel("Ustaw opad (m3/s): ");
            txtRain = new JTextField("0", 5);
            JButton btnSend = new JButton("Wyślij");
            btnSend.setBackground(new Color(52, 152, 219)); // Jasny niebieski
            btnSend.setForeground(Color.WHITE);

            btnSend.addActionListener(e -> sendRain());

            txtRain.addActionListener(e -> sendRain()); // Allow Enter key
            controlPanel.add(lblSet);
            controlPanel.add(txtRain);
            controlPanel.add(btnSend);

            // Dół - status
            lblStatus = new JLabel("Gotowy do symulacji.");
            lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 10));
            lblStatus.setForeground(Color.GRAY);
            lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);

            add(lblIcon, BorderLayout.WEST);
            add(controlPanel, BorderLayout.CENTER);
            add(lblStatus, BorderLayout.SOUTH);
        }

        private void sendRain() {
            try {
                int val = Integer.parseInt(txtRain.getText());

                new Thread(() -> {
                    try {
                        river.stub().setRainfall(val);
                        SwingUtilities.invokeLater(() -> {
                            lblStatus.setText("Wysłano: " + val + " | Godzina: " + java.time.LocalTime.now());
                            lblStatus.setForeground(new Color(39, 174, 96)); // Zielony
                        });
                    } catch (RemoteException e) {
                        SwingUtilities.invokeLater(() -> {
                            lblStatus.setText("Błąd RMI: " + e.getMessage());
                            lblStatus.setForeground(Color.RED);
                        });
                        e.printStackTrace();
                    }
                }).start();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "To nie jest liczba!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
