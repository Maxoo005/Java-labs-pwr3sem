package org.gui.app;

import org.gui.view.MainWindow;
import javax.swing.*;

public class SimulationApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
