package de.therapeutenkiller.dominium.persistenz.jpa;

import de.therapeutenkiller.dominium.modell.Schnappschuss;
import de.therapeutenkiller.dominium.persistenz.EreignisstromNichtVorhanden;
import de.therapeutenkiller.dominium.persistenz.jpa.testaggregat.TestAggregat;
import de.therapeutenkiller.dominium.persistenz.jpa.testaggregat.TestSchnappschuss;
import de.therapeutenkiller.testing.DatenbankRegel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public final class SchnappschussInEinemEreignislagerAblegen {
    @Rule
    public DatenbankRegel datenbankRegel = new DatenbankRegel();

    private HibernateEventStore<TestAggregat, Long> store;
    private TestUhr uhr = new TestUhr();

    @SuppressWarnings("LawOfDemeter")
    private TestSchnappschuss testSchnappschuss = new TestSchnappschuss(
            TestSchnappschuss.createInitializer()
            .identitätsmerkmal(1L)
            .version(1L)
            .zustand(42L));

    @Before
    public void angenommen_ich_habe_einen_ereignisstrom_angelegt() {
        this.store = new HibernateEventStore<>(this.datenbankRegel.getEntityManager(), this.uhr);
        this.store.neuenEreignisstromErzeugen("test-strom", new ArrayList<>());

        this.uhr.stellen(LocalDateTime.now());
    }

    @Test
    public void wenn_ich_einen_schnappschuss_ablege() throws IOException, EreignisstromNichtVorhanden {

        this.store.schnappschussHinzufügen("test-strom", this.testSchnappschuss);
        this.dann_werde_ich_das_aggregat_mit_schnappschuss_wiederherstellen();
    }

    private void dann_werde_ich_das_aggregat_mit_schnappschuss_wiederherstellen() throws EreignisstromNichtVorhanden {

        final Schnappschuss<TestAggregat, Long> neuesterSchnappschuss = this.store.getNeuesterSchnappschuss(
                "test-strom").get();

        assertThat(neuesterSchnappschuss).isEqualTo(this.testSchnappschuss);
    }

    @Test
    public void wenn_ich_einen_schnappschuss_in_einem_nicht_vorhandenen_ereignisstrom_ablege() throws IOException {
        final Throwable thrown = catchThrowable(() -> {
            this.store.schnappschussHinzufügen("nicht-vorhanden", this.testSchnappschuss);
        });

        this.dann_werde_ich_eine_ereignisstromNichtVorhanden_ausnahme_erhalten(thrown);
    }

    private void dann_werde_ich_eine_ereignisstromNichtVorhanden_ausnahme_erhalten(final Throwable thrown) {
        assertThat(thrown)
                .isExactlyInstanceOf(EreignisstromNichtVorhanden.class);
    }
}
