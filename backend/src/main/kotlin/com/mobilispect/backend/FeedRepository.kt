package com.mobilispect.backend

import com.mobilispect.backend.schedule.feed.Feed
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface FeedRepository : Repository<Feed, String> {
    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    fun findAll(): List<Feed>

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
    fun save(entity: Feed): Feed

    /**
     * Deletes all entities.
     *
     * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version
     * attribute with a different value from that found in the persistence store. Also thrown if at least one
     * entity is assumed to be present but does not exist in the database.
     */
    fun deleteAll()
}
