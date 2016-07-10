package com.github.haschi.haushaltsbuch.domaene;

import com.github.haschi.haushaltsbuch.api.kommando.BeginneHaushaltsbuchfuehrung;
import com.github.haschi.haushaltsbuch.api.kommando.ImmutableBeginneHaushaltsbuchfuehrung;
import com.github.haschi.haushaltsbuch.domaene.aggregat.ereignis.HauptbuchWurdeAngelegt;
import com.github.haschi.haushaltsbuch.domaene.aggregat.ereignis.ImmutableHauptbuchWurdeAngelegt;
import com.github.haschi.haushaltsbuch.domaene.aggregat.ereignis.ImmutableJournalWurdeAngelegt;
import com.github.haschi.haushaltsbuch.domaene.aggregat.ereignis.JournalWurdeAngelegt;
import com.github.haschi.haushaltsbuch.domaene.testsupport.DieWelt;
import cucumber.api.PendingException;
import cucumber.api.java.de.Dann;
import cucumber.api.java.de.Wenn;
import org.axonframework.commandhandling.gateway.CommandGateway;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Singleton
public final class HaushaltsbuchführungBeginnenSteps {

    @Inject
    private CommandGateway commandGateway;

    @Inject
    DieWelt kontext;

    @Wenn("^ich mit der Haushaltsbuchführung beginne$")
    public void ich_mit_der_Haushaltsbuchführung_beginne() {
        final UUID haushaltsbuchId = UUID.randomUUID();
        this.commandGateway.sendAndWait(ImmutableBeginneHaushaltsbuchfuehrung.builder().id(haushaltsbuchId).build());
        this.kontext.setAktuelleHaushaltsbuchId(haushaltsbuchId);
    }

    @Dann("^werde ich ein neues Haushaltsbuch angelegt haben")
    public void dann_werde_ich_ein_neues_haushaltsbuch_angelegt_haben()  {
        throw new PendingException();
    }

    @Dann("^werde ich ein Hauptbuch mit Kontenrahmen zum Haushaltsbuch angelegt haben")
    public void dann_werde_ich_ein_hauptbuch_mit_kontenrahmen_zum_haushaltsbuch_anlegen() {

        assertThat(this.kontext.aktuellerEreignisstrom())
                .contains(ImmutableHauptbuchWurdeAngelegt.builder()
                    .haushaltsbuchId(this.kontext.getAktuelleHaushaltsbuchId())
                    .build());
    }

    @Dann("^werde ich ein Journal zum Haushaltsbuch angelegt haben")
    public void dann_werde_ich_ein_journal_zum_haushaltsbuch_anlegen() {
        assertThat(this.kontext.aktuellerEreignisstrom())
                .contains(ImmutableJournalWurdeAngelegt.builder()
                    .aktuelleHaushaltsbuchId(this.kontext.getAktuelleHaushaltsbuchId())
                    .build());
    }
}
