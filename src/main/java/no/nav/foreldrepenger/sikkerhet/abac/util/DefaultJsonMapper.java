package no.nav.foreldrepenger.sikkerhet.abac.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import no.nav.vedtak.exception.TekniskException;

public class DefaultJsonMapper {

    private DefaultJsonMapper() {

    }

    public static final ObjectMapper MAPPER = getObjectMapper();

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public static <T> List<T> fromJson(String json, TypeReference<List<T>> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new TekniskException("F-919328", "Fikk IO exception ved parsing av JSON", e);
        }
    }

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            throw new TekniskException("F-208314", "Kunne ikke serialisere objekt til JSON", e);
        }
    }
}
