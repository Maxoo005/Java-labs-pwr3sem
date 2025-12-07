package org.example.model;

public class PassengerWorkData {

    private final int id;
    private final String port;
    private final int year;
    private final String month;
    private final double importValue;
    private final double exportValue;

    public PassengerWorkData(int id, String port, int year, String month, double importValue, double exportValue) {
        this.id = id;
        this.port = port;
        this.year = year;
        this.month = month;
        this.importValue = importValue;
        this.exportValue = exportValue;
    }

    public int getId() {
        return id;
    }
    public String getPort() {
        return port;
    }
    public int getYear() {
        return year;
    }
    public String getMonth() {
        return month;
    }
    public double getImportValue() {
        return importValue;
    }
    public double getExportValue() {
        return exportValue;
    }

    @Override
    public String toString() {
        return "PassengerWorkData{" +
                "id=" + id +
                ", port='" + port + '\'' +
                ", year=" + year +
                ", month='" + month + '\'' +
                ", importValue=" + importValue +
                ", exportValue=" + exportValue +
                '}';
    }
}