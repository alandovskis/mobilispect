package com.mobilispect.backend.data.stop

import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface StopRepository : Repository<Stop, String> {
    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    fun findAll(): List<Stop>

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed
     * the entity instance completely.
     *
     * @param entity must not be null.
     * @return the saved entity; will never be null.
     * @throws IllegalArgumentException in case the given entity is null.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute
     * with a different value from that found in the persistence store. Also thrown if the entity is assumed to be
     * present but does not exist in the database.
     */
    fun save(entity: Stop): Stop
}
