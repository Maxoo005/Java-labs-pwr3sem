package org.gui;

import org.example.model.PassengerWorkData;
import org.example.service.PassengerWorkService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PassengerWorkGUI extends JFrame {

    private final PassengerWorkService service;

    private JTable table;
    private JComboBox<String> portCombo;
    private JComboBox<String> yearCombo;
    private JButton filterButton;
    private JButton resetButton;
    private JRadioButton fileRadio;
    private JRadioButton apiRadio;

    // pełne dane, to co wyświetlamy i wgl
    private List<PassengerWorkData> allData = new ArrayList<>();

    public PassengerWorkGUI() {
        this.service = new PassengerWorkService();

        setTitle("Praca przewozowa pasażerów (TranStat)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //ładowanie danych z pliku bez api
        allData = service.loadFromSampleFile();

        //Budowanie Gui
        initComponents();

        //Dane początkowe tabeli
        updateTable(allData);
    }

    private void initComponents() {
        //Panel z opisami i filtrami
        JPanel northPanel = new JPanel(new BorderLayout());

        //opis wskaźnika
        JLabel descLabel = new JLabel(
                "Wskaźnik 1-2-6: Praca przewozowa pasażerów w relacji z portem morskim " +
                        "(pasażerokilometry, dane miesięczne TranStat GUS)"
        );
        descLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN, 11f));
        northPanel.add(descLabel, BorderLayout.NORTH);

        // Panel z filtrami port rok przyciski
        JPanel filterPanel = createFilterPanel();
        northPanel.add(filterPanel, BorderLayout.CENTER);

        // Panel z wyborem źródła danych (plik / API)
        JPanel sourcePanel = createSourcePanel();
        northPanel.add(sourcePanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        //główna tablea
        table = new JTable();
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true); // sortowanie po nagłówkach

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // zbiory portów i lat
        Set<String> ports = new LinkedHashSet<>();
        Set<Integer> years = new LinkedHashSet<>();
        for (PassengerWorkData d : allData) {
            ports.add(d.getPort());
            years.add(d.getYear());
        }

        //lista z portem (combox)
        panel.add(new JLabel("Port:"));
        portCombo = new JComboBox<>();
        portCombo.addItem("Wszystkie");
        for (String p : ports) {
            portCombo.addItem(p);
        }
        panel.add(portCombo);

        // combox rok lista rozwijana
        panel.add(new JLabel("Rok:"));
        yearCombo = new JComboBox<>();
        yearCombo.addItem("Wszystkie");
        for (Integer y : years) {
            yearCombo.addItem(String.valueOf(y));
        }
        panel.add(yearCombo);

        //Przyciski
        filterButton = new JButton("Filtruj");
        resetButton = new JButton("Reset");

        filterButton.addActionListener(e -> applyFilter());

        resetButton.addActionListener(e -> {
            portCombo.setSelectedIndex(0);
            yearCombo.setSelectedIndex(0);
            updateTable(allData);
        });

        panel.add(filterButton);
        panel.add(resetButton);

        return panel;
    }

    private JPanel createSourcePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        panel.add(new JLabel("Źródło danych:"));

        fileRadio = new JRadioButton("Dane z pliku (offline)", true);
        apiRadio = new JRadioButton("Dane z API TranStat (online)");

        ButtonGroup group = new ButtonGroup();
        group.add(fileRadio);
        group.add(apiRadio);

        //przeładowanie adnych po zmianie źródla
        fileRadio.addActionListener(e -> {
            if (fileRadio.isSelected()) {
                loadFromFileAndRefresh();
            }
        });

        apiRadio.addActionListener(e -> {
            if (apiRadio.isSelected()) {
                loadFromApiAndRefresh();
            }
        });

        panel.add(fileRadio);
        panel.add(apiRadio);

        return panel;
    }

    //Wczytanie z pliku JSON i odświeżenie tabeli.
    private void loadFromFileAndRefresh() {
        allData = service.loadFromSampleFile();
        // po zmianie źródła resetujemy filtry
        portCombo.setSelectedIndex(0);
        yearCombo.setSelectedIndex(0);
        updateTable(allData);
    }


     //Wczytanie z API TranStat i odświeżenie tabeli.
     //Jeśli coś pójdzie nie tak, wracamy do danych z pliku.
    private void loadFromApiAndRefresh() {
        try {
            allData = service.loadFromApi();
            portCombo.setSelectedIndex(0);
            yearCombo.setSelectedIndex(0);
            updateTable(allData);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Nie udało się pobrać danych z API.\n"
                            + "Sprawdź połączenie z internetem lub limit zapytań.\n"
                            + "Wracam do danych z pliku.",
                    "Błąd API",
                    JOptionPane.ERROR_MESSAGE
            );
            fileRadio.setSelected(true);
            loadFromFileAndRefresh();
        }
    }


    //Nakłada filtry z comboboxów i odświeża tabelę.

    private void applyFilter() {
        String selectedPort = (String) portCombo.getSelectedItem();
        String selectedYear = (String) yearCombo.getSelectedItem();

        List<PassengerWorkData> filtered = new ArrayList<>();

        for (PassengerWorkData d : allData) {
            boolean portOk = selectedPort == null
                    || selectedPort.equals("Wszystkie")
                    || d.getPort().equals(selectedPort);

            boolean yearOk = selectedYear == null
                    || selectedYear.equals("Wszystkie")
                    || String.valueOf(d.getYear()).equals(selectedYear);

            if (portOk && yearOk) {
                filtered.add(d);
            }
        }

        updateTable(filtered);
    }


    //Ustawiennie danych w tabeli.

    private void updateTable(List<PassengerWorkData> data) {
        String[] columns = {"ID", "Port", "Rok", "Miesiąc", "Import", "Export"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (PassengerWorkData d : data) {
            model.addRow(new Object[]{
                    d.getId(),
                    d.getPort(),
                    d.getYear(),
                    d.getMonth(),
                    d.getImportValue(),
                    d.getExportValue()
            });
        }

        table.setModel(model);

        //szerokości kolumn upiększenie
        if (table.getColumnCount() >= 6) {
            table.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
            table.getColumnModel().getColumn(1).setPreferredWidth(120); // Port
            table.getColumnModel().getColumn(2).setPreferredWidth(60);  // Rok
            table.getColumnModel().getColumn(3).setPreferredWidth(60);  // Miesiąc
        }
    }
}
