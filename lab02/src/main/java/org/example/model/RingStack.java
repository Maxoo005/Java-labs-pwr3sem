package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//stos pierścieni przypisany do jednego otworu
public class RingStack {
    private final Hole hole;
    private final List<Ring> rings = new ArrayList<>();

    public RingStack(Hole hole) {
        this.hole = hole;
    }
    //pobranie i przypisanie hole, dostęp do listy pierścieni
    public Hole hole() {
        return hole;
    }
    public List<Ring> rings() {
        return rings;
    }
    //dodawanie pierścienia do stosu na wierzch
    public void add(Ring r) {
        rings.add(r);
    }
    //wysokość stosu
    public int heightSum() {
        return rings.stream().mapToInt(Ring::height).sum();
    }
    //zwrócenie liczny pierścieni
    public int count() {
        return rings.size();
    }
    //czy stos jest zamknięty, inaczej czy jest zakończony krążkiem
    public boolean isClosed() {
        return !rings.isEmpty() && rings.get(rings.size() - 1).isDisk();
    }
    //pomoc w przejrzystym wypisaniu zawartości do konsoli
    public String pretty() {
        String ids = rings.stream().map(r -> String.valueOf(r.id())).collect(Collectors.joining(", "));
        return "Otwór " + hole.id() + " (R=" + hole.radius() + "): [" + ids + "]  H=" + heightSum() + ", C=" + count();
    }
}
