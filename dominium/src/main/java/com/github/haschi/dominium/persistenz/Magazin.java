package com.github.haschi.dominium.persistenz;

import com.github.haschi.dominium.modell.Version;
import com.github.haschi.dominium.modell.Aggregatwurzel;
import com.github.haschi.dominium.modell.Domänenereignis;
import com.github.haschi.dominium.modell.Schnappschuss;
import com.github.haschi.dominium.modell.Versionsbereich;

import java.util.List;
import java.util.Optional;

/**
 *
 * @param <A> Der Typ der Aggregate, die im Magazin abgelegt werden.
 * @param <I> Der Typ des Identitätsmerkmals der Aggregate
 * @param <T> Der Typ der Schnittstelle, auf die Domänenereignisse des Aggregats angewendet werden.
 */
@SuppressWarnings("checkstyle:designforextension")
public abstract class Magazin<A extends Aggregatwurzel<A, I, T, S>, I, T, S extends Schnappschuss<I>>
        implements Repository<A, I, T, S> {

    private final Ereignislager<I, T> ereignislager;
    private final SchnappschussLager<S, I> schnappschussLager;

    protected Magazin(
            final Ereignislager<I, T> ereignislager,
            final SchnappschussLager<S, I> schnappschussLager) {

        super();

        this.ereignislager = ereignislager;
        this.schnappschussLager = schnappschussLager;
    }

    @Override
    public A suchen(final I identitätsmerkmal) throws AggregatNichtGefunden {

        final Optional<S> schnappschuss =
                this.schnappschussLager.getNeuesterSchnappschuss(identitätsmerkmal);

        final Version von = schnappschuss.map(Schnappschuss::getVersion).orElse(Version.NEU);
        final Versionsbereich versionsbereich = Versionsbereich.von(von).bis(Version.MAX);

        final A aggregat = this.neuesAggregatErzeugen(identitätsmerkmal, von);
        schnappschuss.ifPresent(aggregat::wiederherstellenAus);

        final List<Domänenereignis<T>> stream = this.ereignislager.getEreignisliste(identitätsmerkmal, versionsbereich);
        aggregat.aktualisieren(stream);

        return aggregat;
    }

    protected abstract A neuesAggregatErzeugen(final I identitätsmerkmal, final Version version);

    @Override
    public void hinzufügen(final A aggregat) {
        final List<Domänenereignis<T>> änderungen = aggregat.getÄnderungen();
        this.ereignislager.neuenEreignisstromErzeugen(aggregat.getIdentitätsmerkmal(), änderungen);
    }

    @Override
    public void speichern(final A aggregat) throws KonkurrierenderZugriff, AggregatNichtGefunden {
        try {
            this.ereignislager.ereignisseDemStromHinzufügen(
                    aggregat.getIdentitätsmerkmal(),
                    aggregat.getInitialversion().alsLong(),
                    aggregat.getÄnderungen()
            );
        } catch (final EreignisstromWurdeNichtGefunden ereignisstromWurdeNichtGefunden) {
            throw new AggregatNichtGefunden(ereignisstromWurdeNichtGefunden);
        }
    }

    public void speichern(final S  schnappschuss) throws AggregatNichtGefunden {
        if (!this.ereignislager.existiertEreignisStrom(schnappschuss.getIdentitätsmerkmal())) {
            throw new AggregatNichtGefunden();
        }

        this.schnappschussLager.schnappschussHinzufügen(schnappschuss, schnappschuss.getIdentitätsmerkmal());
    }

    public void schnappschussSpeichern(final A aggregat) throws ÄnderungenSindVorhandenGewesen {
        if (!aggregat.getÄnderungen().isEmpty()) {
            throw new ÄnderungenSindVorhandenGewesen();
        }

        final S schnappschuss = aggregat.schnappschussErstellen();
        this.schnappschussLager.schnappschussHinzufügen(schnappschuss, schnappschuss.getIdentitätsmerkmal());
    }

    protected Ereignislager<I, T> getEreignislager() {
        return this.ereignislager;
    }
}