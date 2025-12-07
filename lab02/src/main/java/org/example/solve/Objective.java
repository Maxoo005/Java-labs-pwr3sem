package org.example.solve;

public enum Objective {
    //H-wysokość C-ilość
    MINH_MINC("minH-minC"),
    MINH_MAXC("minH-maxC"),
    MAXH_MINC("maxH-minC"),
    MAXH_MAXC("maxH-maxC");
    private final String code;

    //każdy Objective ma swój kod tekstowy
    Objective(String code) {
        this.code = code;
    }
    public String code() {
        return code;
    }

    public static Objective fromCode(String s) {
        for (Objective o : values()) {
            if (o.code.equalsIgnoreCase(s)) return o;
        }
        throw new IllegalArgumentException("Nieznane objective: " + s);
    }
    //true = minimalizacja wysokości
    public boolean minimizeHeight() {
        return this == MINH_MINC || this == MINH_MAXC;
    }
    //true = minimalizacja ilości
    public boolean minimizeCount() {
        return this == MINH_MINC || this == MAXH_MINC;
    }
}
