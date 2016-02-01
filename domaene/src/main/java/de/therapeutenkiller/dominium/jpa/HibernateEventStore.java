package de.therapeutenkiller.dominium.jpa;

import de.therapeutenkiller.dominium.modell.Aggregatwurzel;
import de.therapeutenkiller.dominium.modell.Domänenereignis;
import de.therapeutenkiller.dominium.modell.Schnappschuss;
import de.therapeutenkiller.dominium.persistenz.Ereignislager;
import de.therapeutenkiller.dominium.persistenz.KonkurrierenderZugriff;
import de.therapeutenkiller.dominium.persistenz.Umschlag;
import de.therapeutenkiller.dominium.persistenz.Versionsbereich;
import org.apache.commons.lang3.NotImplementedException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HibernateEventStore<A extends Aggregatwurzel<A, I>, I>
        implements Ereignislager<A, I> {

    private final EntityManager entityManager;

    public HibernateEventStore(final EntityManager entityManager) {

        this.entityManager = entityManager;
    }

    @Override
    public final void neuenEreignisstromErzeugen(
            final String streamName,
            final Collection<Domänenereignis<A>> domänenereignisse) {
        final JpaEreignisstrom<A> ereignisstrom = new JpaEreignisstrom<>(streamName);

        for (final Domänenereignis<A> ereignis : domänenereignisse) {

            final Umschlag<Domänenereignis<A>, JpaEreignisMetaDaten> umschlag = ereignisstrom.registrieren(ereignis);
            this.entityManager.persist(umschlag);
        }

        this.entityManager.persist(ereignisstrom);
    }

    @Override
    public final void ereignisseDemStromHinzufügen(
            final String streamName,
            final Collection<Domänenereignis<A>> domänenereignisse,
            final long erwarteteVersion) throws KonkurrierenderZugriff {

        final JpaEreignisstrom<A> strom = (JpaEreignisstrom<A>)this.entityManager.find(
                JpaEreignisstrom.class,
                streamName);

        for (final Domänenereignis<A> ereignis : domänenereignisse) {
            final Umschlag<Domänenereignis<A>, JpaEreignisMetaDaten> umschlag = strom.registrieren(ereignis);
            this.entityManager.persist(umschlag);
        }

    }

    @Override
    public List<Domänenereignis<A>> getEreignisListe(String streamName, Versionsbereich bereich) {
        final TypedQuery<JpaDomänenereignisUmschlag> query = this.entityManager.createQuery(
                "SELECT i FROM JpaDomänenereignisUmschlag i "
                        + "WHERE i.version >= :vonVersion AND i.version <= :bisVersion",
                JpaDomänenereignisUmschlag.class);

        query.setParameter("vonVersion", vonVersion);
        query.setParameter("bisVersion", bisVersion);

        final List<JpaDomänenereignisUmschlag> resultList = query.getResultList();

        return resultList.stream()
                .map(DomänenereignisUmschlag::getEreignis)
                .map(ereignis -> (Domänenereignis<A>)ereignis)
                .collect(Collectors.toList());
    }

    @Override
    public void schnappschussHinzufügen(String streamName, Schnappschuss<A, I> snapshot) {

    }

    @Override
    public final List<Domänenereignis<A>> getEreignisListe(
            final String streamName,
            final long vonVersion,
            final long bisVersion) {

        final TypedQuery<JpaDomänenereignisUmschlag> query = this.entityManager.createQuery(
                "SELECT i FROM JpaDomänenereignisUmschlag i "
                + "WHERE i.version >= :vonVersion AND i.version <= :bisVersion",
                JpaDomänenereignisUmschlag.class);

        query.setParameter("vonVersion", vonVersion);
        query.setParameter("bisVersion", bisVersion);

        final List<JpaDomänenereignisUmschlag> resultList = query.getResultList();

        return resultList.stream()
                .map(DomänenereignisUmschlag::getEreignis)
                .map(ereignis -> (Domänenereignis<A>)ereignis)
                .collect(Collectors.toList());
    }

    @Override
    public final void schnappschussHinzufügen(final String streamName, final E snapshot) {
        throw new NotImplementedException("Nicht implementiert.");
    }

    @Override
    public final E getNeuesterSchnappschuss(final String streamName) {
        return null;
    }

    @Override
    public final <T> Initialereignis<A, T> getInitialereignis(final String streamName) {
        final TypedQuery<JpaDomänenereignisUmschlag> query = this.entityManager.createQuery(
                "SELECT i FROM JpaDomänenereignisUmschlag i WHERE i.version = 1",
                JpaDomänenereignisUmschlag.class);

        final DomänenereignisUmschlag umschlag = query.getSingleResult();
        final Domänenereignis ereignis = umschlag.getEreignis();
        return (Initialereignis<A, T>)ereignis; // NOPMD
    }
}
