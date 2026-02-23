package org.example;

public class Protocol {
    // KOMENDY
    public static final String GET_WATER_DISCHARGE = "gwd";
    public static final String GET_FILLING_PERCENTAGE = "gfp";

    // PREFIKSY KOMENDY Z PARAMETRAMI
    public static final String SET_WATER_DISCHARGE = "swd:";
    public static final String SET_WATER_INFLOW = "swi:";
    public static final String SET_REAL_DISCHARGE = "srd:";
    public static final String SET_RAINFALL = "srf:";

    public static final String ASSIGN_RETENTION_BASIN = "arb:";
    public static final String ASSIGN_RIVER_SECTION = "ars:";

    // SEPARATOR
    public static final String SEPARATOR = ",";

    // HELPER- tworzy odpowiedz zgodną z protokołem
    public static String createResponse(int value) {
        return "{" + value + "}";
    }

    // Pomocnicza komenda by wyciągnąc wartość z komendy
    public static String getValueFromCommand(String message, String prefix) {
        if (message.startsWith(prefix)) {
            return message.substring(prefix.length());
        }
        return "";
    }
}