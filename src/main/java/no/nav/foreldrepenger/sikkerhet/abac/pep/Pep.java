package no.nav.foreldrepenger.sikkerhet.abac.pep;

import no.nav.foreldrepenger.sikkerhet.abac.domene.Tilgangsbeslutning;
import no.nav.foreldrepenger.sikkerhet.abac.domene.BeskyttRessursAttributer;

public interface Pep {

    Tilgangsbeslutning vurderTilgang(BeskyttRessursAttributer attributter);
}
