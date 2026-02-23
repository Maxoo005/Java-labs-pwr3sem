package org.example;

import javax.swing.*;
import java.awt.*;

public class RetentionBasinGui extends JFrame {
    private final BasinState state;
    private final JProgressBar fillBar;
    private final JLabel lblDischarge;
    private final JLabel lblTargetDischarge;
    private final JLabel lblStatus;

    private final interfaces.ITailor tailor; // Reference to Tailor for dynamic connections
    private final JTextField txtTargetDischarge;
    private final JTextField txtTargetName;

    public RetentionBasinGui(BasinState state, String title, interfaces.ITailor tailor) {
        this.state = state;
        this.tailor = tailor;
        setTitle("Zbiornik: " + title);
        setSize(400, 350); // Increased height for new controls
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Inforamacje, poziom napełnienia
        JPanel center = new JPanel(new GridLayout(6, 1, 5, 5)); // More rows
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        center.add(new JLabel("Poziom wypełnienia:"));
        fillBar = new JProgressBar(0, 100);
        fillBar.setStringPainted(true);
        center.add(fillBar);

        lblDischarge = new JLabel("Aktualny zrzut: 0 m3/s");
        lblDischarge.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(lblDischarge);

        lblTargetDischarge = new JLabel("Planowany zrzut: 0 m3/s");
        lblTargetDischarge.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(lblTargetDischarge);

        // Controla
        JPanel pnlControl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlControl.add(new JLabel("Ustaw zrzut:"));
        txtTargetDischarge = new JTextField("0", 5);
        JButton btnSetDischarge = new JButton("Ustaw");
        btnSetDischarge.addActionListener(e -> setDischarge());
        pnlControl.add(txtTargetDischarge);
        pnlControl.add(btnSetDischarge);
        center.add(pnlControl);

        // połączneie wychodzące
        JPanel pnlConnect = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlConnect.add(new JLabel("Cel (Rzeka):"));
        txtTargetName = new JTextField("", 8);
        JButton btnConnect = new JButton("Połącz");
        btnConnect.addActionListener(e -> connectToOutput());
        pnlConnect.add(txtTargetName);
        pnlConnect.add(btnConnect);
        center.add(pnlConnect);

        add(center, BorderLayout.CENTER);

        //Status
        lblStatus = new JLabel("Status: OK");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lblStatus, BorderLayout.SOUTH);

        Timer timer = new Timer(500, e -> refresh());
        timer.start();
    }

    private void setDischarge() {
        try {
            int val = Integer.parseInt(txtTargetDischarge.getText());
            if (val < 0)
                val = 0;
            state.setTargetDischarge(val);
            JOptionPane.showMessageDialog(this, "Ustawiono planowany zrzut na: " + val);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Błędna liczba!", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void connectToOutput() {
        String targetName = txtTargetName.getText().trim();
        if (targetName.isEmpty())
            return;

        new Thread(() -> {
            try {
                java.rmi.Remote remote = tailor.getRemote(targetName);
                if (remote instanceof interfaces.IRiverSection) {
                    state.setRiverOut((interfaces.IRiverSection) remote);
                    SwingUtilities
                            .invokeLater(() -> JOptionPane.showMessageDialog(this, "Połączono z rzeką: " + targetName));
                } else if (remote == null) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                            "Nie znaleziono: " + targetName, "Błąd", JOptionPane.ERROR_MESSAGE));
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                            "Obiekt " + targetName + " nie jest rzeką!", "Błąd", JOptionPane.ERROR_MESSAGE));
                }
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Błąd połączenia: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void refresh() {
        long fill = state.getFillingPercentage();
        int flow = state.getRealDischarge();
        int target = state.getTargetDischarge();

        fillBar.setValue((int) fill);
        fillBar.setString(fill + "%");

        if (fill < 50)
            fillBar.setForeground(new Color(0, 150, 0));
        else if (fill < 80)
            fillBar.setForeground(Color.ORANGE);
        else
            fillBar.setForeground(Color.RED);

        lblDischarge.setText("Aktualny zrzut: " + flow + " m3/s");
        lblTargetDischarge.setText("Planowany zrzut: " + target + " m3/s");

        interfaces.IRiverSection out = state.getRiverOut();
        if (out != null) {
            try {
                lblStatus.setText("Ujście: " + out.getName());
                lblStatus.setForeground(Color.BLACK);
            } catch (Exception e) {
                lblStatus.setText("Ujście: ? (Błąd RMI)");
                lblStatus.setForeground(Color.RED);
            }
        } else {
            lblStatus.setText("Ujście: Brak (Zrzut w pole)");
            lblStatus.setForeground(Color.BLUE);
        }
    }
}
