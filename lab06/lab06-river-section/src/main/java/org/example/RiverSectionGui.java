package org.example;

import javax.swing.*;
import java.awt.*;

public class RiverSectionGui extends JFrame {
    private final RiverState state;
    private final JLabel lblInflow;
    private final JLabel lblRain;
    private final JLabel lblOutflow;
    private final JLabel lblDownstream;

    public RiverSectionGui(RiverState state, String title) {
        this.state = state;
        setTitle("Rzeka: " + title);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1));

        lblInflow = new JLabel("Napływ: 0");
        lblInflow.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblInflow);

        lblRain = new JLabel("Deszcz: 0");
        lblRain.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblRain);

        lblOutflow = new JLabel("Wypływ: 0");
        lblOutflow.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblOutflow);

        lblDownstream = new JLabel("Ujście: Brak");
        lblDownstream.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblDownstream);

        add(panel);

        Timer timer = new Timer(500, e -> refresh());
        timer.start();
    }

    private void refresh() {

        interfaces.IRetensionBasin downstream = state.getDownstreamBasin();
        if (downstream != null) {
            try {
                lblDownstream.setText("Ujście: " + downstream.getName());
                lblDownstream.setForeground(new Color(0, 150, 0));
            } catch (Exception e) {
                lblDownstream.setText("Ujście: Błąd połączenia");
                lblDownstream.setForeground(Color.RED);
            }
        } else {
            lblDownstream.setText("Ujście: Brak (W pustkę)");
            lblDownstream.setForeground(Color.BLACK);
        }
    }

    public void updateValues(int inflow, int rain, int outflow) {
        lblInflow.setText("Napływ: " + inflow);
        lblRain.setText("Deszcz: " + rain);
        lblOutflow.setText("Wypływ: " + outflow);
    }
}
