package de.therapeutenkiller.dominium.persistenz

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll

class VersionsbereichTest extends Specification {

    @Unroll
    def "In einem Versionsbereich (#von, #bis) liegt #zahl innerhalb"() {

        when:
        Versionsbereich versionsbereich = new Versionsbereich(von, bis)

        then:
        versionsbereich.liegtInnerhalb zahl

        where:
        von | bis || zahl
        1   | 10  || 1
        1   | 10  || 5
        1   | 10  || 10
        2   | 999 || 2
        2   | 999 || 500
        2   | 999 || 999
    }

    def "In einem Versionsbereich (#von, #bis) liegt #zahl außerhalb"() {

        when:
        Versionsbereich versionsbereich = new Versionsbereich(von, bis)

        then:
        !(versionsbereich.liegtInnerhalb(zahl))

        where:
        von | bis || zahl
        1   | 10  || 0
        1   | 10  || 11
        1   | 10  || -100
        1   | 10  || 100
        2   | 999 || 1
        2   | 999 || 1000
    }

    @Unroll
    def "Die Zahl #zahl liegt innerhalb des Versionsbereichs ALLE_VERSIONEN"() {
        expect:
        Versionsbereich.ALLE_VERSIONEN.liegtInnerhalb(zahl)

        where:
        zahl | _
        1              | _
        100            | _
        Long.MAX_VALUE | _
    }

    def "Die untere Grenze des Versionsbereichs darf nicht größer als die obere Grenze sein"() {

        when:
        new Versionsbereich(von, bis)

        then:
        thrown(exception)

        where:
        von | bis | exception
        2   | 1   | IllegalArgumentException
        100 | 99  | IllegalArgumentException
    }

    def "Versionsbereiche mit gleichen Grenzen sind identisch"() {
        expect: EqualsVerifier.forClass(Versionsbereich).verify()
    }

    def "Die untere Grenze des Versionsbereichs muss eine positive Zahl sein"() {
        when:
        new Versionsbereich(von, 100)

        then:
        thrown IllegalArgumentException

        where:
        von  | _
        0    | _
        -1   | _
        -100 | _
    }
}