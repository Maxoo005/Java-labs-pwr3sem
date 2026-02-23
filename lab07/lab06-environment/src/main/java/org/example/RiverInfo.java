package org.example;

import interfaces.IRiverSection;

public record RiverInfo(String name, IRiverSection stub) {
    @Override
    public String toString() {
        return "Rzeka: " + name;
    }
}
