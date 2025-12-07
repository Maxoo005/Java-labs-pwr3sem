package org.example.domain.model;

import java.util.Objects;

public class ServicePrice {
    private Long id;          // null przed insert
    private String name;      // np. "Strzyżenie męskie"
    private int priceCents;   // cena w groszach

    public ServicePrice(Long id, String name, int priceCents) {
        this.id = id;
        this.name = name;
        this.priceCents = priceCents;
    }

    public static ServicePrice ofNew(String name, int priceCents) {
        return new ServicePrice(null, name, priceCents);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriceCents(int priceCents) {
        this.priceCents = priceCents;
    }

    @Override public String toString() {
        return "ServicePrice{id=%s, name='%s', priceCents=%d}".formatted(id, name, priceCents);
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServicePrice sp)) return false;
        return Objects.equals(id, sp.id);
    }
    @Override public int hashCode() {
        return Objects.hashCode(id);
    }
}
