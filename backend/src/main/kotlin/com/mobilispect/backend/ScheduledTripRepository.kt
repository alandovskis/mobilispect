package com.mobilispect.backend

import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

interface ScheduledTripRepository : Repository<ScheduledTrip, String> {
    fun findAll(): List<ScheduledTrip>

    @RestResource(exported = false)
    fun save(entity: ScheduledTrip): ScheduledTrip

    @RestResource(exported = false)
    fun deleteAll()
}
