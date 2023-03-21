package com.mobilispect.backend.data.route

import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(path = "routes")
interface ExportedRouteRepository : Repository<Route, String> {
    fun findAllByAgencyID(id: String): List<Route>
}
