package org.example;

import interfaces.IRetensionBasin;

public record BasinInfo(int id, String name, IRetensionBasin stub) {
    @Override
    public String toString() {
        return "Zbiornik #" + id + " (" + name + ")";
    }
}
