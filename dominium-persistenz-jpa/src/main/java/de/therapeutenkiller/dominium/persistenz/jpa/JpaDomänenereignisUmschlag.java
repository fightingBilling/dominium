package de.therapeutenkiller.dominium.persistenz.jpa;

import de.therapeutenkiller.dominium.modell.Domänenereignis;
import de.therapeutenkiller.dominium.modell.Wertobjekt;
import de.therapeutenkiller.dominium.persistenz.Umschlag;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.io.IOException;

/**
 * Ein DomänenereignisUmschlag für Domänenereignisse zum Speichern in einer
 * Datenbank mit JPA.
 * @param <A> Der Typ des Aggregats, dessen Domänenereignisse gekapselt werden.
 */
@Entity
public class JpaDomänenereignisUmschlag<A>
        extends Wertobjekt
        implements Umschlag<Domänenereignis<A>, JpaEreignisMetaDaten> {

    @EmbeddedId
    private JpaEreignisMetaDaten meta = null;

    @Lob
    private byte[] ereignis = null; // NOPMD

    public JpaDomänenereignisUmschlag(
            final Domänenereignis<A> ereignis,
            final JpaEreignisMetaDaten meta) {
        super();

        this.meta = meta;

        try {
            this.ereignis = EventSerializer.serialize(ereignis);
        } catch (final IOException exception) {
            throw new IllegalArgumentException("Das war nix.", exception);
        }
    }

    public JpaDomänenereignisUmschlag() {
        super();
    }

    public final Domänenereignis<A> getEreignis() {
        try {
            return (Domänenereignis<A>) EventSerializer.deserialize(this.ereignis);
        } catch (final IOException exception) {
            throw new IllegalArgumentException("Geht nicht!", exception);
        } catch (final ClassNotFoundException exception) {
            throw new IllegalArgumentException("Geht nicht.!", exception);
        }
    }

    @Override
    public final JpaEreignisMetaDaten getMetaDaten() {
        return this.meta;
    }

    @Override
    public final Domänenereignis<A> öffnen() {
        return this.getEreignis();
    }
}
