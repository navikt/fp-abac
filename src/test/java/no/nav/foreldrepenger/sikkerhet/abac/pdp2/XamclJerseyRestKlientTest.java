package no.nav.foreldrepenger.sikkerhet.abac.pdp2;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Application;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.bridge.SLF4JBridgeHandler;

import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacAttributtNøkkel;
import no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml.Decision;
import no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml.XacmlRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XamclJerseyRestKlientTest extends JerseyTest {

    static {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(XacmlTestServerController.class);
    }

    // do not name this setup()
    @BeforeAll
    public void before() throws Exception {
        super.setUp();
    }

    // do not name this tearDown()
    @AfterAll
    public void after() throws Exception {
        super.tearDown();
    }

    @Test
    public void givenGetHiGreeting_whenCorrectRequest_thenResponseIsOkAndContainsHi() {
        var actionSet = new XacmlRequest.AttributeSet(List.of(new XacmlRequest.Pair(AbacAttributtNøkkel.ACTION_ACTION_ID, "read")));
        var envSet = new XacmlRequest.AttributeSet(
            List.of(
                new XacmlRequest.Pair(AbacAttributtNøkkel.ENVIRONMENT_PEP_ID, "local-app"),
                new XacmlRequest.Pair(AbacAttributtNøkkel.ENVIRONMENT_OIDC_TOKEN_BODY, "eyAiYXRfaGFzaCI6ICIyb2c1RGk5ZW9LeFhOa3VPd0dvVUdBIiwgInN1YiI6ICJzMTQyNDQzIiwgImF1ZGl0VHJhY2tpbmdJZCI6ICI1NTM0ZmQ4ZS03MmE2LTRhMWQtOWU5YS1iZmEzYThhMTljMDUtNjE2NjA2NyIsICJpc3MiOiAiaHR0cHM6Ly9pc3NvLXQuYWRlby5ubzo0NDMvaXNzby9vYXV0aDIiLCAidG9rZW5OYW1lIjogImlkX3Rva2VuIiwgImF1ZCI6ICJPSURDIiwgImNfaGFzaCI6ICJiVWYzcU5CN3dTdi0wVlN0bjhXLURnIiwgIm9yZy5mb3JnZXJvY2sub3BlbmlkY29ubmVjdC5vcHMiOiAiMTdhOGZiMzYtMGI0Ny00YzRkLWE4YWYtZWM4Nzc3Y2MyZmIyIiwgImF6cCI6ICJPSURDIiwgImF1dGhfdGltZSI6IDE0OTgwMzk5MTQsICJyZWFsbSI6ICIvIiwgImV4cCI6IDE0OTgwNDM1MTUsICJ0b2tlblR5cGUiOiAiSldUVG9rZW4iLCAiaWF0IjogMTQ5ODAzOTkxNSB9")
            ));
        var resourceSet = List.of(
            new XacmlRequest.AttributeSet(
                List.of(
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_DOMENE, "foreldrepenger"),
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_RESOURCE_TYPE, "no.nav.abac.attributter.foreldrepenger.fagsak"),
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_PERSON_FNR, "12345678900")
                )),
            new XacmlRequest.AttributeSet(
                List.of(
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_DOMENE, "foreldrepenger"),
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_RESOURCE_TYPE, "no.nav.abac.attributter.foreldrepenger.fagsak"),
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_PERSON_AKTOERID, "11111")
                ))
        );

        XacmlRequest.Request value = new XacmlRequest.Request(actionSet, envSet, resourceSet, null);
        XacmlRequest request = new XacmlRequest(value);

        XamclJerseyRestKlient client = new XamclJerseyRestKlient(URI.create(getBaseUri() + "application/authorize"), "test", "test");
        var evaluate = client.evaluate(request);

        assertThat(evaluate).isNotNull();
        assertThat(evaluate.Response()).hasSize(1);
        assertThat(evaluate.Response().get(0).Decision()).isEqualTo(Decision.Deny);
    }

    @Override
    public TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new TestContainerFactory() {
            @Override
            public TestContainer create(URI baseUri, DeploymentContext deploymentContext) {
                return new TestContainer() {
                    private HttpServer server;

                    @Override
                    public ClientConfig getClientConfig() {
                        return null;
                    }

                    @Override
                    public URI getBaseUri() {
                        return baseUri;
                    }

                    @Override
                    public void start() {
                        try {
                            this.server = GrizzlyWebContainerFactory.create(
                                baseUri, Collections.singletonMap(ServerProperties.PROVIDER_CLASSNAMES, XacmlTestServerController.class.getName())
                            );
                        } catch (ProcessingException | IOException e) {
                            throw new TestContainerException(e);
                        }
                    }

                    @Override
                    public void stop() {
                        this.server.shutdownNow();

                    }
                };
            }
        };
    }
}
