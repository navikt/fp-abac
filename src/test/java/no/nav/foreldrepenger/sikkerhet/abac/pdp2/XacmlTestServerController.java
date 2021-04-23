package no.nav.foreldrepenger.sikkerhet.abac.pdp2;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml.XacmlRequest;
import no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml.XacmlResponse;

@Path("/application")
public class XacmlTestServerController {
    @POST
    @Path("/authorize")
    @Produces(XamclJerseyRestKlient.MEDIA_TYPE)
    @Consumes(XamclJerseyRestKlient.MEDIA_TYPE)
    public XacmlResponse authorize(XacmlRequest request) throws IOException {
        return DefaultJsonMapper.MAPPER.readValue(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("xacmlresponse.json")).getFile()), XacmlResponse.class);
    }
}
