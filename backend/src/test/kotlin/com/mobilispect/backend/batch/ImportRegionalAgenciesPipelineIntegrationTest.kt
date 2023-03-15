package com.mobilispect.backend.batch

import com.mobilispect.backend.data.AgencyRepository
import com.mobilispect.backend.data.agency.FakeRegionalAgencyDataSource
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest


@DataMongoTest
class ImportRegionalAgenciesPipelineIntegrationTest {
    @Autowired
    private lateinit var agencyRepository: AgencyRepository

    private val networkDataSource: RegionalAgencyDataSource = FakeRegionalAgencyDataSource()

    private lateinit var subject: ImportRegionalAgenciesPipeline

    @BeforeEach
    fun prepare() {
        subject = ImportRegionalAgenciesPipeline(agencyRepository, networkDataSource)
    }

    @Test
    fun addsAllMissingAgenciesWhenThereAreNone() {
        val expected = networkDataSource.agencies(apiKey = "apikey", city = "city").getOrNull()!!

        subject.apply("city")

        val actual = agencyRepository.findAll()
        assertThat(actual).containsAll(expected.agencies)
    }

    @Test
    fun addsAnyMissingAgencies() {
        val expected = networkDataSource.agencies(apiKey = "apikey", city = "city").getOrNull()!!
        agencyRepository.save(expected.agencies.first().copy())

        subject.apply("city")

        val actual = agencyRepository.findAll()
        assertThat(actual).containsAll(expected.agencies)
    }

    @Test
    fun doesNothingWhenAllAgenciesArePresent() {
        val expected = networkDataSource.agencies(apiKey = "apikey", city = "city").getOrNull()!!
        for (agency in expected.agencies) {
            agencyRepository.save(agency.copy())
        }

        subject.apply("city")

        val actual = agencyRepository.findAll()
        assertThat(actual).containsAll(expected.agencies)
    }
}
