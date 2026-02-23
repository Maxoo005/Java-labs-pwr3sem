package org.example;

public class ProtocolConstants {
    // Metody
    public static final String METHOD_GET_WATER_DISCHARGE = "gwd";
    public static final String METHOD_GET_FILLING_PERCENTAGE = "gfp";
    
    public static final String METHOD_SET_WATER_DISCHARGE = "swd"; // + :value
    public static final String METHOD_SET_WATER_INFLOW = "swi"; // + :value,port
    public static final String METHOD_SET_REAL_DISCHARGE = "srd"; // + :value
    public static final String METHOD_SET_RAINFALL = "srf"; // + :value
    
    public static final String METHOD_ASSIGN_RETENTION_BASIN = "arb"; // + :port,host
    public static final String METHOD_ASSIGN_RIVER_SECTION = "ars"; // + :port,host
    
    // Odpowiedzi
    public static final String RESPONSE_OK = "OK";
    public static final String RESPONSE_ERROR = "ERROR";
}
