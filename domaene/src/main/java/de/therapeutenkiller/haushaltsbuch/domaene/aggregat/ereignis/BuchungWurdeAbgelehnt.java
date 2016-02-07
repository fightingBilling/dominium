package de.therapeutenkiller.haushaltsbuch.domaene.aggregat.ereignis;

import de.therapeutenkiller.dominium.modell.Wertobjekt;
import de.therapeutenkiller.haushaltsbuch.domaene.aggregat.Haushaltsbuch;
import de.therapeutenkiller.haushaltsbuch.domaene.aggregat.HaushaltsbuchEreignis;

import java.io.Serializable;

public final class BuchungWurdeAbgelehnt extends Wertobjekt implements HaushaltsbuchEreignis, Serializable {
    private static final long serialVersionUID = 1L;

    private final String grund;

    public BuchungWurdeAbgelehnt(final String grund) {

        super();
        this.grund = grund;
    }

    @Override
    public String toString() {
        return String.format("Buchung wurde nicht ausgeführt: %s", this.grund); // NOPMD LoD TODO
    }

    @Override
    public void anwendenAuf(final Haushaltsbuch aggregat) {
        aggregat.falls(this);
    }
}