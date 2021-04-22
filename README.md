![](https://github.com/navikt/fp-abac/workflows/Bygg%20og%20deploy/badge.svg) 
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-abac&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_fp-abac) 
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-abac&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=navikt_fp-abac)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-abac&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=navikt_fp-abac)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-abac&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=navikt_fp-abac)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/navikt/fp-abac)
![GitHub](https://img.shields.io/github/license/navikt/fp-abac)

# Felles ABAC / bibliotek for fpsak som håndterer authentisering og auditloggin.
Inneholder følgende moduler:
* Audit logging med CEF (Common Event Format) som brukes i Arcsight.
* PEP/PDP biblioteker for ABAC tilgangskontroll.

# Migrering
* BeskyttetRessursActionAttributt endrer navn til ActionType
* @BeskyttetRessurs krever en obligatorisk parameter path=/path/til/tjenesten/som/brukes
* @BeskyttetRessurs krever service=ServiceType.WEBSERVICE på alle WS grensesnitt (default: ServiceType.REST)
* Implementer PdpRequestBuilder klasse.
* Endre i koden no.nav.vedtak.sikkerhet.abac.AbacAttributtType til no.nav.foreldrepenger.sikkerhet.abac.domene.AbacAttributtType
* Endre no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling til no.nav.foreldrepenger.sikkerhet.abac.domene.BeskyttRessursAttributer
* Endre no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt til no.nav.foreldrepenger.sikkerhet.abac.domene.ActionType
* Endre no.nav.vedtak.sikkerhet.abac.AbacDto til no.nav.foreldrepenger.sikkerhet.abac.AbacDto
* Endre no.nav.vedtak.sikkerhet.abac.AbacDataAttributter til no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter
* Endre no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt til no.nav.vedtak.sikkerhet.abac.AbacDtoSupplier
