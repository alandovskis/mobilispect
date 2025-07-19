package com.mobilispect.backend

import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(path = "routes")
interface RouteRepository : Repository<Route, String> {
    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    fun findAll(): List<Route>

    @RestResource(exported = false)
    fun save(entity: Route): Route

    /**
     * Deletes all entities.
     *
     * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version
     * attribute with a different value from that found in the persistence store. Also thrown if at least one
     * entity is assumed to be present but does not exist in the database.
     */
    fun deleteAll()

    fun findAllByAgencyID(agencyID: String): List<Route>
}
