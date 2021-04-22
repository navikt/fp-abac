![](https://github.com/navikt/fp-felles/workflows/Bygg%20og%20deploy/badge.svg) 
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-felles&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_fp-felles) 
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-felles&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=navikt_fp-felles)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-felles&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=navikt_fp-felles)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-felles&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=navikt_fp-felles)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/navikt/fp-felles)
![GitHub](https://img.shields.io/github/license/navikt/fp-felles)

# Felles ABAC / bibliotek for fpsak som håndterer authentisering og auditloggin.
Inneholder følgende moduler:
* Audit logging med CEF (Common Event Format) som brukes i Arcsight.
* PEP/PDP biblioteker for ABAC tilgangskontroll.

# Migrering
* Implement TokenProvider
* BeskyttetRessursActionAttributt endrer navn til ActionType
* Legg til type=ServiceType.WEBSERVICE om det er en Webservice eller er REST default til BeskyttRessurs
* Legg til path=/hele/path/til/tjenesten som skal kalles.
* Legg til token i pdpRequestBuilder
