package com.mobilispect.backend

import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource
interface FeedVersionRepository : Repository<FeedVersion, String> {
    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    fun findAll(): List<FeedVersion>

    fun getById(id: String): FeedVersion?

    @RestResource(exported = false)
    fun save(entity: FeedVersion): FeedVersion

    @RestResource(exported = false)
    fun deleteAll()
}
