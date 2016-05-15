package de.therapeutenkiller.dominium.modell;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <A> Der Typ einer konkreten Ableitung von Aggregatwurzel.
 * @param <I> Der Typ des Identitätsmerkmals der Aggregatwurzel.
 * @param <T> Der Schnittstelle des Ereignis-Ziels der Domänenereignisse des Aggregats
 */
public abstract class Aggregatwurzel<A extends Aggregatwurzel<A, I, T, S>, I, T, S extends Schnappschuss<I>>
        extends Entität<I>
        implements SchnappschussQuelle<A, I> {

    private final List<Domänenereignis<T>> änderungen = new ArrayList<>();
    private Version version;
    private Version initialversion;

    protected Aggregatwurzel(final I identitätsmerkmal, final Version version) {
        super(identitätsmerkmal);
        this.version = version;
        this.initialversion = version;
    }

    protected Aggregatwurzel(final Schnappschuss<I> schnappschuss) {
        super(schnappschuss.getIdentitätsmerkmal());
        this.version = schnappschuss.getVersion();
    }

    protected final void bewirkt(final Domänenereignis<T> ereignis) {
        this.änderungen.add(ereignis);
        this.anwenden(ereignis);
    }

    public final void anwenden(final Domänenereignis<T> ereignis) {
        this.version = this.version.nachfolger();
        ereignis.anwendenAuf(this.getSelf());
    }

    private void anwenden(final List<Domänenereignis<T>> ereignisse) {
        for (final Domänenereignis<T> ereignis : ereignisse) {
            this.anwenden(ereignis);
        }
    }

    public final void aktualisieren(final List<Domänenereignis<T>> stream) {
        this.anwenden(stream);
        this.setInitialversion(this.getVersion());
    }

    // Die nachfolgenden zwei Methoden implementieren das Memento Muster.
    // Können diese in eine eigene Schnittstelle ausgelagert werden?
    public abstract void wiederherstellenAus(final S schnappschuss);

    public abstract S schnappschussErstellen();

    public final List<Domänenereignis<T>> getÄnderungen() {
        return this.änderungen;
    }

    protected abstract T getSelf();

    public final long getVersion() {
        return this.version.alsLong();
    }

    public final void setInitialversion(final long initialversion) {
        this.initialversion = new Version(initialversion);
    }

    public final long getInitialversion() {
        return this.initialversion.alsLong();
    }
}

