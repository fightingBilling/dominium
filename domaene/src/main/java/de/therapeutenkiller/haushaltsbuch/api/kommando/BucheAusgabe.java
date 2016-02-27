package de.therapeutenkiller.haushaltsbuch.api.kommando;

import de.therapeutenkiller.dominium.modell.Wertobjekt;

import javax.money.MonetaryAmount;
import java.util.UUID;

public class BucheAusgabe extends Wertobjekt {
    public final UUID haushaltsbuchId;
    public final String sollkonto;
    public final String habenkonto;
    public final MonetaryAmount währungsbetrag;

    public BucheAusgabe(
            final UUID haushaltsbuchId,
            final String sollkonto,
            final String habenkonto,
            final MonetaryAmount währungsbetrag) {

        super();

        this.haushaltsbuchId = haushaltsbuchId;
        this.sollkonto = sollkonto;
        this.habenkonto = habenkonto;
        this.währungsbetrag = währungsbetrag;
    }
}