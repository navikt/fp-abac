package no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml;

import java.util.List;


public record XacmlRequest(Request Request) {
    public static record Request (
        AttributeSet Action,
        AttributeSet Environment,
        List<AttributeSet> Resource,
        AttributeSet AccessSubject) {
    }
    public static record AttributeSet(List<Pair> Attribute) {}
    public static record Pair(String AttributeId, Object Value) {}
}
