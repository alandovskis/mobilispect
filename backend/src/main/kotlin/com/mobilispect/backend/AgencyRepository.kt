package com.mobilispect.backend

import org.springframework.data.repository.Repository
import org.springframework.data.rest.core.annotation.RestResource

interface AgencyRepository : Repository<Agency, String> {
    fun findAll(): List<Agency>

    @RestResource(exported = false)
    fun save(agency: Agency): Agency

    @RestResource(exported = false)
    fun deleteAll()
}