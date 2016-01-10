package de.therapeutenkiller.haushaltsbuch.domaene.support;

/**
 * Schnittstelle für Domänenereignis-Umschläge. Der Umschlag  kapselt ein Domänenereignis und
 * fügt Meta-Informationen hinzu.
 *
 * @param <A> Der Typ der Aggregatwurzel, auf dass sich das gekapselte Domänenereignis bezieht.
 */
public interface Umschlag<A> {
    Domänenereignis<A> getEreignis();

    int getVersion();

    String getStreamName();
}
