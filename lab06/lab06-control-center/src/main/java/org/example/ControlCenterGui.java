package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;
import interfaces.IRetensionBasin;

public class ControlCenterGui extends JFrame {

    private final ControlState state;
    private final JPanel basinsPanel;
    private final Map<Integer, BasinPanel> basinUiMap = new HashMap<>();

    public ControlCenterGui(ControlState state) {
        this.state = state;

        // Ustawienia Okna
        setTitle("System Hydrologiczny - Panel Sterowania (RMI)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Wygląd
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }

        // Nagłówek
        JPanel header = new JPanel();
        header.setBackground(new Color(40, 40, 40));
        JLabel title = new JLabel("MONITORING ZBIORNIKÓW");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        header.add(title);

        add(header, BorderLayout.NORTH);

        // Lista przewijana
        basinsPanel = new JPanel();
        basinsPanel.setLayout(new BoxLayout(basinsPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(basinsPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // TIMER odświerzanie co sekunde
        Timer timer = new Timer(1000, e -> refreshData());
        timer.start();
    }

    private void refreshData() {
        // Pobieramy listę zbiorników, które zgłosiły się do serwera
        for (BasinInfo basin : state.getBasins()) {

            // Jeśli nie mamy jeszcze kafelka dla tego ID
            if (!basinUiMap.containsKey(basin.id())) {
                BasinPanel panel = new BasinPanel(basin);
                basinUiMap.put(basin.id(), panel);
                basinsPanel.add(panel);
                basinsPanel.add(Box.createVerticalStrut(10));
                basinsPanel.revalidate();
            }

            // Każemy kafelkowi pobrać nowe dane z sieci
            basinUiMap.get(basin.id()).updateFromNetwork();
        }
        repaint();
    }

    // Wygląd kafelka
    private class BasinPanel extends JPanel {
        private final BasinInfo basin;
        private final JProgressBar bar;
        private final JLabel lblInfo;
        private final JLabel lblStatus;
        private final JTextField txtSetFlow;

        public BasinPanel(BasinInfo basin) {
            this.basin = basin;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.BLUE, 2),
                    " " + basin.toString() + " "));
            setBackground(Color.WHITE);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            // Pasek postępu
            bar = new JProgressBar(0, 100);
            bar.setStringPainted(true);
            bar.setFont(new Font("Arial", Font.BOLD, 14));

            // Info tekstowe
            lblInfo = new JLabel("Czekam na dane...");

            JPanel center = new JPanel(new GridLayout(2, 1));
            center.add(bar);
            center.add(lblInfo);
            add(center, BorderLayout.CENTER);

            // Sterowanie (Prawa strona)
            JPanel right = new JPanel();
            right.add(new JLabel("Ustaw zrzut:"));
            txtSetFlow = new JTextField("0", 4);
            right.add(txtSetFlow);
            JButton btn = new JButton("Wyślij");
            btn.addActionListener(e -> sendFlowCommand());
            right.add(btn);
            add(right, BorderLayout.EAST);

            // Status
            lblStatus = new JLabel("Łączenie...");
            lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 10));
            add(lblStatus, BorderLayout.SOUTH);
        }

        // To jest kluczowa metoda - pobiera dane z sieci
        public void updateFromNetwork() {
            new Thread(() -> {
                try {
                    IRetensionBasin stub = basin.stub();
                    // Zapytanie o % napełnienia
                    long fill = stub.getFillingPercentage();
                    // Zapytanie o zrzut
                    int flow = stub.getWaterDischarge();

                    // potwierdzenie działania
                    // System.out.println("[GUI DEBUG] Otrzymano od zbiornika: Fill=" + fill + ",
                    // Flow=" + flow);

                    SwingUtilities.invokeLater(() -> {
                        // Aktualizacja wyglądu
                        bar.setValue((int) fill);
                        bar.setString(fill + "%");

                        // Kolorki
                        if (fill < 50)
                            bar.setForeground(new Color(0, 150, 0));
                        else if (fill < 80)
                            bar.setForeground(Color.ORANGE);
                        else
                            bar.setForeground(Color.RED);

                        lblInfo.setText("Aktualny zrzut wody: " + flow + " m3/s");
                        lblStatus.setText("Ostatnia aktualizacja: " + java.time.LocalTime.now());
                        lblStatus.setForeground(Color.BLACK);
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText("BRAK ODPOWIEDZI (ZBIORNIK OFFLINE?) - " + e.getMessage());
                        lblStatus.setForeground(Color.RED);
                    });
                }
            }).start();
        }

        private void sendFlowCommand() {
            try {
                int val = Integer.parseInt(txtSetFlow.getText());
                new Thread(() -> {
                    try {
                        basin.stub().setWaterDischarge(val);
                        SwingUtilities
                                .invokeLater(() -> JOptionPane.showMessageDialog(this, "Wysłano polecenie: " + val));
                    } catch (Exception e) {
                        SwingUtilities.invokeLater(
                                () -> JOptionPane.showMessageDialog(this, "Błąd wysyłania: " + e.getMessage()));
                    }
                }).start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Wpisz liczbę!");
            }
        }
    }
}