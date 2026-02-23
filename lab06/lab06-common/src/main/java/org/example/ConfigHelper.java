package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class ConfigHelper {

    // Okienko startowe i wybrany port
    public static int showStartupDialog(String appName, int defaultPort) {
        String myIp = getRealIpAddress();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Twoje IP:"));
        JTextField txtIp = new JTextField(myIp);
        txtIp.setEditable(false);
        txtIp.setForeground(new Color(0, 100, 0));
        txtIp.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(txtIp);

        panel.add(new JLabel("Port nasłuchu:"));
        JTextField txtPort = new JTextField(String.valueOf(defaultPort));
        panel.add(txtPort);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Start: " + appName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) System.exit(0);

        try {
            return Integer.parseInt(txtPort.getText().trim());
        } catch (Exception e) {
            return defaultPort;
        }
    }

    // Sprawdzanie prawdziwego IP
    public static String getRealIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.getAddress().length == 4 && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) { }
        return "localhost";
    }
}
