package org.gui;

public class MainGUI {
    public static void main(String[] args) {
        new PassengerWorkGUI().setVisible(true);
    }
}

/*
* Uruchamianie aplikacji z lini komend
*
* mvn clean package
* cd dist
* java -p "lab04_client-1.0-SNAPSHOT.jar;lab04_gui-1.0-SNAPSHOT.jar;json-20230227.jar" -m lab04.gui/org.gui.MainGUI
*
* */