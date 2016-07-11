package testsupport;

import com.github.haschi.haushaltsbuch.domaene.aggregat.ereignis.HaushaltsbuchEreignis;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class DieWelt {

    private UUID aktuelleHaushaltsbuchId;

    private final Map<String, List<HaushaltsbuchEreignis>> ereignisse = new HashMap<>();

    public List<HaushaltsbuchEreignis> aktuellerEreignisstrom() {
        return this.getStream(this.getAktuelleHaushaltsbuchId());
    }

    public List<HaushaltsbuchEreignis> getStream(final UUID haushaltsbuchId) {
        return this.ereignisse.get(haushaltsbuchId.toString());
    }

    public UUID getAktuelleHaushaltsbuchId() {
        return this.aktuelleHaushaltsbuchId;
    }

    public void setAktuelleHaushaltsbuchId(final UUID aktuelleHaushaltsbuchId) {
        this.aktuelleHaushaltsbuchId = aktuelleHaushaltsbuchId;
    }

    @EventHandler
    public void falls(final HaushaltsbuchEreignis ereignis, final DomainEventMessage<HaushaltsbuchEreignis> message) {

        final String aggregateIdentifier = message.getAggregateIdentifier().toString();

        if (!this.ereignisse.containsKey(aggregateIdentifier)) {
            this.ereignisse.put(aggregateIdentifier, new ArrayList<>());
        }

        final List<HaushaltsbuchEreignis> haushaltsbuchEreignises = this.ereignisse.get(aggregateIdentifier);
        haushaltsbuchEreignises.add(ereignis);
    }
}