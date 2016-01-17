package de.therapeutenkiller.haushaltsbuch.api.ereignis;

import de.therapeutenkiller.haushaltsbuch.domaene.aggregat.Buchungssatz;
import de.therapeutenkiller.haushaltsbuch.domaene.aggregat.Haushaltsbuch;
import de.therapeutenkiller.haushaltsbuch.domaene.aggregat.HaushaltsbuchEreignis;
import de.therapeutenkiller.dominium.aggregat.Wertobjekt;

import javax.money.MonetaryAmount;
import java.io.Serializable;

public final class BuchungWurdeAusgeführt extends Wertobjekt implements HaushaltsbuchEreignis, Serializable {

    private static final long serialVersionUID = 1L;

    public final String soll;
    public final String haben;
    public final MonetaryAmount betrag;

    public BuchungWurdeAusgeführt(final String soll, final String haben, final MonetaryAmount betrag) {

        super();

        this.soll = soll;
        this.haben = haben;
        this.betrag = betrag;
    }

    public Buchungssatz getBuchungssatz() {
        return new Buchungssatz(this.soll, this.haben, this.betrag);
    }

    @Override
    public void anwendenAuf(final Haushaltsbuch aggregat) {
        aggregat.falls(this);
    }
}
