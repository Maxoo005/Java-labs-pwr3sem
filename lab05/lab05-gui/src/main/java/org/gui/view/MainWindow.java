package org.gui.view;

import org.example.board.Board;
import org.example.board.Cell;
import org.example.board.BoardListener;
import org.example.controller.SimulatorController;
import org.example.model.FigureType;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame implements BoardListener {

    private final JLabel treasureLabel = new JLabel("Zebrane skarby: 0");
    private static final int DEFAULT_WIDTH = 8;
    private static final int DEFAULT_HEIGHT = 8;

    private final Board board;
    private final JLabel[][] cellLabels;

    // symulator od logiki lab05-core
    private final SimulatorController controller;

    public MainWindow() {
        super("Symulator figur – Lab 5");

        // tworzenie logiki board i listenera
        this.board = new Board(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.board.setListener(this);

        // kotroloer
        this.controller = new SimulatorController(board);

        //tablica
        this.cellLabels = new JLabel[DEFAULT_HEIGHT][DEFAULT_WIDTH];

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //siatka
        JPanel gridPanel = new JPanel(new GridLayout(DEFAULT_HEIGHT, DEFAULT_WIDTH));
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);

        for (int y = 0; y < DEFAULT_HEIGHT; y++) {
            for (int x = 0; x < DEFAULT_WIDTH; x++) {
                JLabel label = new JLabel(" ", SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                label.setFont(font);
                cellLabels[y][x] = label;
                gridPanel.add(label);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        //przyciski i licznik
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());

        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(treasureLabel);

        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void startSimulation() {
        controller.start(
                700,   // delay kreatora
                0.5,   // skarb
                0.3,   // szperacz
                0.2,   // strzelec
                0.5    // spychacz
        );
    }

    private void stopSimulation() {
        controller.stop();

        // czystka
        SwingUtilities.invokeLater(() -> {
            for (int y = 0; y < DEFAULT_HEIGHT; y++) {
                for (int x = 0; x < DEFAULT_WIDTH; x++) {
                    cellLabels[y][x].setText(" ");
                    cellLabels[y][x].setBackground(Color.WHITE);
                }
            }
        });
    }

    @Override
    public void cellUpdated(int x, int y, Cell cell) {
        if (!board.inBounds(x, y)) return;

        SwingUtilities.invokeLater(() -> {
            JLabel label = cellLabels[y][x];
            label.setOpaque(true);

            String text = " ";
            Color bg = Color.WHITE;

            boolean effectApplied = false;

            // strzał /laser
            if (cell.hasLaserEffect()) {
                text = "•";
                bg = Color.RED;
                effectApplied = true;
            }

            // spychanie
            if (cell.hasPushEffect()) {
                text = "↦";
                bg = Color.CYAN;
                effectApplied = true;
            }

            // brak efektu baisic stan pola
            if (!effectApplied) {

                if (cell.hasTreasure()) {
                    text = "$";
                    bg = new Color(255, 230, 150); //zółty
                }

                if (cell.isCreatorHere()) {
                    text = "C";
                    bg = new Color(200, 200, 255); //niebieskie
                }

                if (cell.getFigure() != null) {
                    FigureType t = cell.getFigure().getType();
                    switch (t) {
                        case SEEKER -> {
                            text = "S";
                            bg = new Color(180, 255, 180); //zielone
                        }
                        case SHOOTER -> {
                            text = "F";
                            bg = new Color(255, 180, 180); //czerwoe
                        }
                        case PUSHER -> {
                            text = "P";
                            bg = new Color(200, 200, 200);  //szare
                        }
                    }
                }
            }

            label.setText(text);
            label.setBackground(bg);
        });
    }

    @Override
    public void treasureCountChanged(int count) {
        SwingUtilities.invokeLater(() ->
                treasureLabel.setText("Zebrane skarby: " + count)
        );
    }
}
