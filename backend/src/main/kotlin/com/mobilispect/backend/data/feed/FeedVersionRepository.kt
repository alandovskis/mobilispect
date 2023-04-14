package com.mobilispect.backend.data.feed

import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface FeedVersionRepository : Repository<FeedVersion, String> {
    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    fun findAll(): List<FeedVersion>

    /**
     * Returns all instances of the type `T` with the given IDs.
     *
     *
     * If some or all ids are not found, no entities are returned for these IDs.
     *
     *
     * Note that the order of elements in the result is not guaranteed.
     *
     * @param ids must not be null nor contain any null values.
     * @return guaranteed to be not null. The size can be equal or less than the number of given
     * ids.
     * @throws IllegalArgumentException in case the given [ids][Iterable] or one of its items is null.
     */
    fun findAllById(ids: Iterable<String>): List<FeedVersion>

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
    fun save(entity: FeedVersion): FeedVersion

    /**
     * Deletes all entities.
     *
     * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version
     * attribute with a different value from that found in the persistence store. Also thrown if at least one
     * entity is assumed to be present but does not exist in the database.
     */
    fun deleteAll()
}
