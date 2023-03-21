package com.mobilispect.backend.data.agency

import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

/**
 * Agency repository operations that exposed as part of the API.
 */
@RepositoryRestResource(path = "agencies")
interface ExportedAgencyRepository : Repository<Agency, String> {
    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    fun findAll(): List<Agency>
}
