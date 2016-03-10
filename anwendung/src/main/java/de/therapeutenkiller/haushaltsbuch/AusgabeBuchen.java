package de.therapeutenkiller.haushaltsbuch;

import de.therapeutenkiller.haushaltsbuch.api.kommando.BucheAusgabe;

import javax.enterprise.event.Event;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@ViewScoped
@ManagedBean
@SuppressWarnings("checkstyle:designforextension")
public class AusgabeBuchen implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id = "";

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    private HauptbuchAnsicht hauptbuch;

    @Inject
    private HauptbuchAbfrage abfrage;

    public void init() {
        this.hauptbuch = this.abfrage.abfragen(UUID.fromString(this.id));
    }

    public List<String> getAktivkonten() {
        return this.hauptbuch.aktivkonten;
    }

    public String getSollkonto() {
        return this.sollkonto;
    }

    public void setSollkonto(final String sollkonto) {
        this.sollkonto = sollkonto;
    }

    private String sollkonto = "";

    public List<String> getAufwandskonten() {
        return this.hauptbuch.aufwandskonten;
    }

    public String getHabenkonto() {
        return this.habenkonto;
    }

    public void setHabenkonto(final String habenkonto) {
        this.habenkonto = habenkonto;
    }

    private String habenkonto = "";

    public String getBetrag() {
        return this.betrag;
    }

    public void setBetrag(final String betrag) {
        this.betrag = betrag;
    }

    private String betrag = "";

    @Inject
    private Event<BucheAusgabe> bucheAusgabeEvent;

    public String ausführen() {
        final MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(Locale.GERMANY);
        final MonetaryAmount währungsbetrag = format.parse(this.betrag + " EUR");

        final BucheAusgabe befehl = new BucheAusgabe(
                UUID.fromString(this.id),
                this.sollkonto,
                this.habenkonto,
                währungsbetrag);

        this.bucheAusgabeEvent.fire(befehl);
        return String.format("hauptbuch?faces-redirect=true&id=%s", this.id);
    }
}