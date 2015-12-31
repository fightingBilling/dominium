package de.therapeutenkiller.haushaltsbuch.domaene.testsupport;

import de.therapeutenkiller.haushaltsbuch.api.ereignis.HaushaltsbuchWurdeAngelegt;
import de.therapeutenkiller.haushaltsbuch.domaene.aggregat.Haushaltsbuch;
import de.therapeutenkiller.haushaltsbuch.domaene.support.Domänenereignis;
import de.therapeutenkiller.haushaltsbuch.domaene.support.EventStore;
import de.therapeutenkiller.haushaltsbuch.domaene.support.Haushaltsbuchereignis;
import de.therapeutenkiller.haushaltsbuch.domaene.support.Haushaltsbuchsnapshot;
import de.therapeutenkiller.haushaltsbuch.spi.HaushaltsbuchRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class HaushaltsbuchMemoryRepository implements HaushaltsbuchRepository {

    private final EventStore<Haushaltsbuchereignis, Haushaltsbuchsnapshot, HaushaltsbuchWurdeAngelegt, Haushaltsbuch> store;

    public final UUID getAktuell() {
        return this.aktuell;
    }

    private UUID aktuell;

    @Override
    public final void leeren() {
    }

    @Inject
    public HaushaltsbuchMemoryRepository(
            final EventStore<Haushaltsbuchereignis, Haushaltsbuchsnapshot, HaushaltsbuchWurdeAngelegt, Haushaltsbuch> store) {

        this.store = store;
    }

    @Override
    public final Haushaltsbuch findBy(final UUID id) {
        final String streamName = this.streamNameFor(id);

        int fromEventNumber = 0;
        final int toEventNumber = Integer.MAX_VALUE;

        final Haushaltsbuchsnapshot snapshot = this.store.getLatestSnapshot(streamName);

        if (snapshot != null) {
            fromEventNumber = snapshot.version + 1; // load only events after snapshot
        }

        final List<Domänenereignis<Haushaltsbuch>> stream = this.store.getStream(streamName, fromEventNumber, toEventNumber);

        Haushaltsbuch haushaltsbuch = null;
        if (snapshot != null) {
            haushaltsbuch = new Haushaltsbuch(snapshot);
        } else {
            Domänenereignis<Haushaltsbuch> ereignis = this.store.getInitialEvent(streamName);
            haushaltsbuch = new Haushaltsbuch((HaushaltsbuchWurdeAngelegt)ereignis); // TODO: kein Cast
        }


        for (final Domänenereignis<Haushaltsbuch> ereignis : stream) {
            ereignis.applyTo(haushaltsbuch);
        }

        return haushaltsbuch;
    }

    private String streamNameFor(final UUID id) {
        // Stream per-aggregate: {AggregateType}-{AggregateId}
        return String.format("%s-%s", Haushaltsbuch.class.getName(), id);
    }

    @Override
    public final void add(final Haushaltsbuch haushaltsbuch) {
        final String streamName = this.streamNameFor(haushaltsbuch.getIdentität());
        final List<Domänenereignis<Haushaltsbuch>> änderungen = haushaltsbuch.getÄnderungen();
        this.store.createNewStream(streamName, änderungen);
        this.aktuell = haushaltsbuch.getIdentität();
    }

    @Override
    public final void save(final Haushaltsbuch haushaltsbuch) {
        final String streamName = this.streamNameFor(haushaltsbuch.getIdentität());

        final Optional<Integer> expectedVersion = this.getExpectedVersion(haushaltsbuch.initialVersion);
        this.store.appendEventsToStream(streamName, haushaltsbuch.getÄnderungen(), expectedVersion);
    }

    private Optional<Integer> getExpectedVersion(final int expectedVersion) {
        if (expectedVersion == 0) {
            // first time the aggregate is stored there is no expected version
            return Optional.empty();
        } else {
            return Optional.of(expectedVersion);
        }
    }

    public final List<Domänenereignis<Haushaltsbuch>> getStream(final UUID haushaltsbuchId) {
        final String streamName = this.streamNameFor(haushaltsbuchId);
        return this.store.getStream(streamName, 0, Integer.MAX_VALUE);
    }
}
