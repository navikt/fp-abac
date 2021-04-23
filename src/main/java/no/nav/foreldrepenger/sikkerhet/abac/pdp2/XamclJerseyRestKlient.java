package no.nav.foreldrepenger.sikkerhet.abac.pdp2;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.apache.connector.ApacheHttpClientBuilderConfigurator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml.XacmlRequest;
import no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml.XacmlResponse;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyRestClient;

@ApplicationScoped
class XamclJerseyRestKlient extends AbstractJerseyRestClient implements NyXacmlConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(XamclJerseyRestKlient.class);
    private static final String DEFAULT_ABAC_URL = "http://abac-foreldrepenger.teamabac/application/authorize";
    private static final String PDP_ENDPOINT_URL_KEY = "abac.pdp.endpoint.url";
    private static final String SYSTEMBRUKER_USERNAME = "systembruker.username";
    private static final String SYSTEMBRUKER_PASSWORD = "systembruker.password"; // NOSONAR
    public static final String MEDIA_TYPE = "application/json";

    private final URI endpoint;
    private final WebTarget target;

    @Inject
    public XamclJerseyRestKlient(
        @KonfigVerdi(value = PDP_ENDPOINT_URL_KEY, defaultVerdi = DEFAULT_ABAC_URL) URI endpoint,
        @KonfigVerdi(SYSTEMBRUKER_USERNAME) String brukernavn,
        @KonfigVerdi(SYSTEMBRUKER_PASSWORD) String passord) {
        super();
        this.endpoint = endpoint;
        target = client
            .register(HttpAuthenticationFeature.basic(brukernavn, passord))
            .register((ApacheHttpClientBuilderConfigurator) b -> b.setDefaultHeaders(List.of()))
            .target(endpoint);
    }

    @Override
    public XacmlResponse evaluate(final XacmlRequest request) {
        try {
            if (Environment.current().isDev()) {
                LOG.trace("PDP request: {}", request);
            }
            return target.request(MEDIA_TYPE)
                .post(Entity.entity(request, MEDIA_TYPE))
                .readEntity(XacmlResponse.class);
        } catch (Exception e) {
            LOG.info("Exception: Kunne ikke evaluere ABAC.", e);
            throw e;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [uri=" + endpoint + "]";
    }
}
