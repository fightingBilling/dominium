package de.therapeutenkiller.dominium.persistenz.jpa;

import de.therapeutenkiller.dominium.modell.Domänenereignis;
import de.therapeutenkiller.dominium.persistenz.KonkurrierenderZugriff;
import de.therapeutenkiller.dominium.persistenz.Uhr;
import de.therapeutenkiller.dominium.persistenz.Versionsbereich;
import de.therapeutenkiller.dominium.persistenz.jpa.testaggregat.TestAggregat;
import de.therapeutenkiller.dominium.persistenz.jpa.testaggregat.ZustandWurdeGeändert;
import de.therapeutenkiller.testing.DatenbankRegel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(JUnit4.class)
public final class EreignisseEinemVorhandenenEreignislagerHinzufügen {

    @Rule
    public DatenbankRegel datenbankRegel = new DatenbankRegel();

    private EntityManager entityManager;
    private JpaEreignislager<TestAggregat, Long> store;

    private Uhr uhr = new TestUhr();

    @Before
    public void angenommen_ich_habe_einen_ereignisstrom_mit_ereignissen_angelegt() throws KonkurrierenderZugriff {
        final TestAggregat aggregat = new TestAggregat(1L);
        aggregat.einenZustandÄndern(42L);
        aggregat.einenZustandÄndern(43L);

        this.entityManager = this.datenbankRegel.getEntityManager();
        this.store = new JpaEreignislager<>(this.entityManager, this.uhr);
        this.store.neuenEreignisstromErzeugen("test-strom", new ArrayList<Domänenereignis<TestAggregat>>() {
            {
                add(new ZustandWurdeGeändert(42L));
                add(new ZustandWurdeGeändert(43L));
            }
        });
    }

    @Test
    public void wenn_ich_dem_ereignislager_weitere_ereignisse_hinzufüge() throws KonkurrierenderZugriff {

        this.store.ereignisseDemStromHinzufügen("test-strom", 3L, new ArrayList<Domänenereignis<TestAggregat>>() {
            {
                add(new ZustandWurdeGeändert(44L));
                add(new ZustandWurdeGeändert(45L));
            }
        });

        this.entityManager.flush();
        this.entityManager.clear();

        this.dann_werden_die_ereignisse_dem_ereignisstrom_hinzugefügt_worden_sein();
    }

    @Test
    public void wenn_ich_dem_ereignislager_nicht_die_korrekte_version_beim_hinzufügen_weiterer_ereignisse_übergebe()
            throws KonkurrierenderZugriff {
        final Throwable thrown = catchThrowable(() -> {
            this.store.ereignisseDemStromHinzufügen(
                    "test-strom",
                    2L,
                    new ArrayList<>());
        });

        this.dann_werde_ich_eine_konkurrierender_zugriff_ausnahme_erhalten(thrown);
    }

    private void dann_werde_ich_eine_konkurrierender_zugriff_ausnahme_erhalten(final Throwable thrown) {
        assertThat(thrown).isExactlyInstanceOf(KonkurrierenderZugriff.class);
    }

    private void dann_werden_die_ereignisse_dem_ereignisstrom_hinzugefügt_worden_sein() {
        final List<Domänenereignis<TestAggregat>> ereignisListe = this.store.getEreignisListe(
                "test-strom",
                Versionsbereich.ALLE_VERSIONEN);

        assertThat(ereignisListe).containsExactly(
                new ZustandWurdeGeändert(42L),
                new ZustandWurdeGeändert(43L),
                new ZustandWurdeGeändert(44L),
                new ZustandWurdeGeändert(45L));
    }
}
