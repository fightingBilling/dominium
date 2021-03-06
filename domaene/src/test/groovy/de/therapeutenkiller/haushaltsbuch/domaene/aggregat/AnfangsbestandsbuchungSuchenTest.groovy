package de.therapeutenkiller.haushaltsbuch.domaene.aggregat
import spock.lang.Specification


class AnfangsbestandsbuchungSuchenTest extends Specification {

    def "Suche nach Anfangsbestandsbuchung"() {

            given:
            def haushaltsbuch = new Haushaltsbuch()
            haushaltsbuch.neueBuchungHinzufügen("Girokonto", Konto.ANFANGSBESTAND.bezeichnung, 10.00.euro)

            expect:
            haushaltsbuch.istAnfangsbestandFürKontoVorhanden("Girokonto")
    }
}
