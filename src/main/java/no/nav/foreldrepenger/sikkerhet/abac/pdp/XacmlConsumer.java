package no.nav.foreldrepenger.sikkerhet.abac.pdp;

import no.nav.foreldrepenger.sikkerhet.abac.pdp.xacml.XacmlRequestBuilder;
import no.nav.foreldrepenger.sikkerhet.abac.pdp.xacml.XacmlResponseWrapper;
import no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml.XacmlRequest;
import no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml.XacmlResponse;

public interface XacmlConsumer {
    XacmlResponseWrapper evaluate(XacmlRequestBuilder request);
}
