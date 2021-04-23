package no.nav.foreldrepenger.sikkerhet.abac.pdp2.xacml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacAttributtNøkkel;

public class XacmlRequestTest {

    private static final SimpleModule SER_DESER = createModule();

    @Test
    void serializeTest() throws IOException {
        var expectedResult = "{\"Request\":{\"Action\":{\"Attribute\":[{\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:action:action-id\",\"Value\":\"read\"}]},\"Environment\":{\"Attribute\":[{\"AttributeId\":\"no.nav.abac.attributter.environment.felles.pep_id\",\"Value\":\"local-app\"},{\"AttributeId\":\"no.nav.abac.attributter.environment.felles.oidc_token_body\",\"Value\":\"eyAiYXRfaGFzaCI6ICIyb2c1RGk5ZW9LeFhOa3VPd0dvVUdBIiwgInN1YiI6ICJzMTQyNDQzIiwgImF1ZGl0VHJhY2tpbmdJZCI6ICI1NTM0ZmQ4ZS03MmE2LTRhMWQtOWU5YS1iZmEzYThhMTljMDUtNjE2NjA2NyIsICJpc3MiOiAiaHR0cHM6Ly9pc3NvLXQuYWRlby5ubzo0NDMvaXNzby9vYXV0aDIiLCAidG9rZW5OYW1lIjogImlkX3Rva2VuIiwgImF1ZCI6ICJPSURDIiwgImNfaGFzaCI6ICJiVWYzcU5CN3dTdi0wVlN0bjhXLURnIiwgIm9yZy5mb3JnZXJvY2sub3BlbmlkY29ubmVjdC5vcHMiOiAiMTdhOGZiMzYtMGI0Ny00YzRkLWE4YWYtZWM4Nzc3Y2MyZmIyIiwgImF6cCI6ICJPSURDIiwgImF1dGhfdGltZSI6IDE0OTgwMzk5MTQsICJyZWFsbSI6ICIvIiwgImV4cCI6IDE0OTgwNDM1MTUsICJ0b2tlblR5cGUiOiAiSldUVG9rZW4iLCAiaWF0IjogMTQ5ODAzOTkxNSB9\"}]},\"Resource\":[{\"Attribute\":[{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.domene\",\"Value\":\"foreldrepenger\"},{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.resource_type\",\"Value\":\"no.nav.abac.attributter.foreldrepenger.fagsak\"},{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.person.fnr\",\"Value\":\"12345678900\"}]},{\"Attribute\":[{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.domene\",\"Value\":\"foreldrepenger\"},{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.resource_type\",\"Value\":\"no.nav.abac.attributter.foreldrepenger.fagsak\"},{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.person.aktoerId_resource\",\"Value\":\"11111\"}]}]}}";

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

        var mapper = DefaultJsonMapper.MAPPER;
        String answer = mapper.writeValueAsString(request);

  //      var mapper2 = new ObjectMapper();
  //      mapper2.registerModule(SER_DESER);
  //      String answer2 = mapper2.writeValueAsString(request);

        assertThat(answer).contains(AbacAttributtNøkkel.ENVIRONMENT_OIDC_TOKEN_BODY);
        assertThat(answer).doesNotContain("AccessSubject");
        assertThat(answer).isEqualTo(expectedResult);
//        assertThat(answer).isEqualTo(answer2);
    }

    @Test
    void serializeWithSubjecTest() throws JsonProcessingException {
        var expectedResult = "{\"Request\":{\"Action\":{\"Attribute\":[{\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:action:action-id\",\"Value\":\"read\"}]},\"Environment\":{\"Attribute\":[{\"AttributeId\":\"no.nav.abac.attributter.environment.felles.pep_id\",\"Value\":\"fpsak\"},{\"AttributeId\":\"no.nav.abac.attributter.environment.felles.tokenx_token_body\",\"Value\":\"blatoken\"}]},\"Resource\":[{\"Attribute\":[{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.domene\",\"Value\":\"foreldrepenger\"},{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.resource_type\",\"Value\":\"no.nav.abac.attributter.foreldrepenger.fagsak\"},{\"AttributeId\":\"no.nav.abac.attributter.resource.felles.person.fnr\",\"Value\":\"12345678900\"}]}],\"AccessSubject\":{\"Attribute\":[{\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\",\"Value\":\"sdfsdf\"},{\"AttributeId\":\"no.nav.abac.attributter.subject.felles.subjectType\",\"Value\":\"EksternBruker\"},{\"AttributeId\":\"no.nav.abac.attributter.subject.felles.authenticationLevel\",\"Value\":4}]}}}";

        var actionSet = new XacmlRequest.AttributeSet(List.of(new XacmlRequest.Pair(AbacAttributtNøkkel.ACTION_ACTION_ID, "read")));
        var envSet = new XacmlRequest.AttributeSet(
            List.of(
                new XacmlRequest.Pair(AbacAttributtNøkkel.ENVIRONMENT_PEP_ID, "fpsak"),
                new XacmlRequest.Pair(AbacAttributtNøkkel.ENVIRONMENT_TOKENX_TOKEN_BODY, "blatoken")
            ));
        var resourceSet = List.of(
            new XacmlRequest.AttributeSet(
                List.of(
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_DOMENE, "foreldrepenger"),
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_RESOURCE_TYPE, "no.nav.abac.attributter.foreldrepenger.fagsak"),
                    new XacmlRequest.Pair(AbacAttributtNøkkel.RESOURCE_PERSON_FNR, "12345678900")
                ))
        );

        var subjectSet = new XacmlRequest.AttributeSet(
            List.of(
                new XacmlRequest.Pair(AbacAttributtNøkkel.SUBJECT_ID, "sdfsdf"),
                new XacmlRequest.Pair(AbacAttributtNøkkel.SUBJECT_TYPE, "EksternBruker"),
                new XacmlRequest.Pair(AbacAttributtNøkkel.SUBJECT_LEVEL, 4)
            ));

        XacmlRequest.Request value = new XacmlRequest.Request(actionSet, envSet, resourceSet, subjectSet);
        XacmlRequest request = new XacmlRequest(value);

        var mapper = DefaultJsonMapper.MAPPER;
        String answer = mapper.writeValueAsString(request);

        assertThat(answer).contains("AccessSubject");
        assertThat(answer).contains("\"Value\":4");
        assertThat(answer).contains(AbacAttributtNøkkel.ENVIRONMENT_TOKENX_TOKEN_BODY);
        assertThat(answer).isEqualTo(expectedResult);
    }

    private static SimpleModule createModule() {
        SimpleModule module = new SimpleModule("VL-REST", new Version(1, 0, 0, null, null, null));

        addSerializers(module);
        addDeserializers(module);

        return module;
    }

    private static void addSerializers(SimpleModule module) {
        module.addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        module.addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
    }

    private static void addDeserializers(SimpleModule module) {
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
    }
}
