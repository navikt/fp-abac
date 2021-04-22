package no.nav.foreldrepenger.sikkerhet.abac.domene;

import java.net.URI;
import java.util.Set;

import com.nimbusds.jwt.SignedJWT;

public class BeskyttRessursAttributer {

    private final AbacDataAttributter dataAttributter = AbacDataAttributter.opprett();
    private ActionType actionType;
    private ServiceType serviceType;
    private String resource;
    private String requestPath;

    public BeskyttRessursAttributer() {
    }

    public BeskyttRessursAttributer leggTil(AbacDataAttributter dataAttributter) {
        this.dataAttributter.leggTil(dataAttributter);
        return this;
    }

    public <T> Set<T> getVerdier(AbacAttributtType type) {
        return dataAttributter.getVerdier(type);
    }

    public Set<AbacAttributtType> keySet() {
        return dataAttributter.keySet();
    }

    @Override
    public String toString() {
        return BeskyttRessursAttributer.class.getSimpleName() + '{' +
            " actionType='" + actionType + "'" +
            " serviceType='" + serviceType + "'" +
            " resource='" + resource + "' " +
            " requestPath='" + requestPath + "'" +
            dataAttributter +
            '}';
    }

    public BeskyttRessursAttributer setActionType(ActionType actionType) {
        this.actionType = actionType;
        return this;
    }

    public ActionType ActionType() {
        return actionType;
    }

    public BeskyttRessursAttributer setServiceType(final ServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public BeskyttRessursAttributer setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String Resource() {
        return resource;
    }

    public BeskyttRessursAttributer setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public String getRequestPath() {
        return requestPath;
    }

    private static TokenType oidcTokenType(String token) {
        try {
            return URI.create(SignedJWT.parse(token)
                .getJWTClaimsSet().getIssuer()).getHost().contains("tokendings") ? TokenType.TOKENX : TokenType.OIDC;

        } catch (Exception e) {
            throw new IllegalArgumentException("Ukjent token type");
        }
    }

}
