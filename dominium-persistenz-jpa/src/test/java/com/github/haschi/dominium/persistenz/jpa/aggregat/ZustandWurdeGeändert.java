package com.github.haschi.dominium.persistenz.jpa.aggregat;

import com.github.haschi.coding.aspekte.ValueObject;
import com.github.haschi.dominium.modell.EreignisZiel;
import org.apache.commons.lang3.builder.ToStringBuilder;

@ValueObject(exclude = "id")
public class ZustandWurdeGeändert implements TestAggregatEreignis {

    private static final long serialVersionUID = 5189914021752101788L;

    private long payload;

    private ZustandWurdeGeändert() {
        super();
    }

    public ZustandWurdeGeändert(final long payload) {
        super();
        this.payload = payload;
    }

    public final long getPayload() {
        return this.payload;
    }

    @Override
    public final void anwendenAuf(final TestAggregatEreignisziel ereignisZiel) {
        ereignisZiel.falls(this);
    }

    @Override
    public void anwendenAuf(final EreignisZiel<TestAggregatEreignisziel> ereignisZiel) {
        ereignisZiel.falls(this);
    }

    @Override
    public final String toString() {
        return new ToStringBuilder(this)
                .append("payload", this.payload)
                .toString();
    }
}
