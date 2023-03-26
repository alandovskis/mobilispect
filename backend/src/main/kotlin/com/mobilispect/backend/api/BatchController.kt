package com.mobilispect.backend.api

import com.mobilispect.backend.batch.ImportRegionalAgenciesService
import com.mobilispect.backend.batch.ImportRoutesService
import com.mobilispect.backend.batch.ImportStopsService
import com.mobilispect.backend.batch.ImportUpdatedFeedsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

private val AGENCIES = arrayOf(
    "o-f25f-rseaudetransportdelongueuil",
    "o-f25g-exo~omitsainte~julie",
    "o-f256-exo~citlapresquîle",
    "o-f25f-exo~citchambly~richelieu~carignan",
    "o-f25d-exo~citroussillon",
    "o-f25u-exo~mrcdelassomption",
    "o-f259-exo~citduhaut~saint~laurent",
    "o-f257-exo~citlaurentides",
    "o-f-viarail",
    "o-f25s-exo~mrclesmoulinsurbis",
    "o-f25d-socitdetransportdemontral",
    "o-f25d-exo~citlerichelain",
    "o-f253-exo~citsud~ouest",
    "o-f25-exo~reseaudetransportmetropolitain",
    "o-f25-exo~citsorel~varennes",
    "o-f25g-exo~citvallée~du~richelieu",
    "o-f25e-societedetransportdelaval",
)

@RestController
class BatchController(
    private val agencyService: ImportRegionalAgenciesService,
    private val routesService: ImportRoutesService,
    private val stopsService: ImportStopsService,
    private val importUpdatedFeedsService: ImportUpdatedFeedsService
) {
    @PostMapping("/batch/import/feeds")
    fun importUpdatedFeeds() {
        importUpdatedFeedsService.get()
    }

    @PostMapping("/batch/import/agencies")
    fun importAgencies() {
        agencyService.apply("Montréal")
    }

    @PostMapping("/batch/import/routes")
    fun importRoutes() {
        AGENCIES.forEach { agencyID -> routesService.apply(agencyID) }
    }

    @PostMapping("/batch/import/stops")
    fun importStops() {
        AGENCIES.forEach { agencyID -> stopsService.apply(agencyID) }
    }
}