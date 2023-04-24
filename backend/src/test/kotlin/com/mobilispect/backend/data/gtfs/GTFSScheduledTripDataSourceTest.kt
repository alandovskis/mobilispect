package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.route.OneStopRouteID
import com.mobilispect.backend.data.schedule.ScheduledTrip
import com.mobilispect.backend.util.copyResourceTo
import kotlinx.serialization.SerializationException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import java.io.IOException
import java.nio.file.Path
import java.time.LocalDate

private const val VERSION: String = "v1"
private const val FEED_ID: String = "feed_id"

@SpringBootTest
internal class GTFSScheduledTripDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    private val subject = GTFSScheduledTripDataSource(TestRouteIDDataSource())

    @Test
    fun bothCalendarFilesNotFound(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:gtfs/citpi-trips.txt", root = root, dst = "trips.txt")

        val result = subject.trips(root.toString(), VERSION, FEED_ID).exceptionOrNull()

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        resourceLoader.copyResourceTo(
            src = "classpath:gtfs/citpi-calendar-dates.txt",
            root = root,
            dst = "calendar_dates.txt"
        )
        resourceLoader.copyResourceTo(src = "classpath:gtfs/citpi-calendar.txt", root = root, dst = "calendar.txt")
        resourceLoader.copyResourceTo(src = "classpath:gtfs/citpi-trips-corrupt.txt", root = root, dst = "trips.txt")

        val result = subject.trips(root.toString(), VERSION, FEED_ID).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun dayOfWeekNotFound(@TempDir root: Path) {
        resourceLoader.copyResourceTo(
            src = "classpath:gtfs/citpi-calendar-dates.txt",
            root = root,
            dst = "calendar_dates.txt"
        )
        resourceLoader.copyResourceTo(
            src = "classpath:gtfs/citpi-calendar-day-of-week-not-found.txt",
            root = root,
            dst = "calendar.txt"
        )
        resourceLoader.copyResourceTo(src = "classpath:gtfs/citpi-trips.txt", root = root, dst = "trips.txt")

        subject.trips(root.toString(), VERSION, FEED_ID).getOrNull()!!
    }

    @Test
    @Suppress("LongMethod")
    fun importsSuccessfully(@TempDir root: Path) {
        resourceLoader.copyResourceTo(
            src = "classpath:gtfs/citpi-calendar-dates.txt",
            root = root,
            dst = "calendar_dates.txt"
        )
        resourceLoader.copyResourceTo(src = "classpath:gtfs/citpi-calendar.txt", root = root, dst = "calendar.txt")
        resourceLoader.copyResourceTo(src = "classpath:gtfs/citpi-trips.txt", root = root, dst = "trips.txt")

        val trips = subject.trips(root.toString(), VERSION, FEED_ID).getOrNull()!!

        assertThat(trips).contains(
            ScheduledTrip(
                _id = "3281905-PI-A22-PI_GTFS-Semaine-01",
                routeID = OneStopRouteID("r-f2566-1"),
                direction = "Seigneurie - Joseph-Carrier AM",
                version = VERSION,
                dates = listOf(
                    LocalDate.of(2022, 11, 23),
                    LocalDate.of(2022, 11, 24),
                    LocalDate.of(2022, 11, 25),

                    LocalDate.of(2022, 11, 28),
                    LocalDate.of(2022, 11, 29),
                    LocalDate.of(2022, 11, 30),
                    LocalDate.of(2022, 12, 1),
                    LocalDate.of(2022, 12, 2),

                    LocalDate.of(2022, 12, 5),
                    LocalDate.of(2022, 12, 6),
                    LocalDate.of(2022, 12, 7),
                    LocalDate.of(2022, 12, 8),
                    LocalDate.of(2022, 12, 9),

                    LocalDate.of(2022, 12, 12),
                    LocalDate.of(2022, 12, 13),
                    LocalDate.of(2022, 12, 14),
                    LocalDate.of(2022, 12, 15),
                    LocalDate.of(2022, 12, 16),

                    LocalDate.of(2022, 12, 19),
                    LocalDate.of(2022, 12, 20),
                    LocalDate.of(2022, 12, 21),
                    LocalDate.of(2022, 12, 22),
                    LocalDate.of(2022, 12, 23),
                )
            )
        )
        assertThat(trips).contains(
            ScheduledTrip(
                _id = "3282456-PI-A22-PI_GTFS-Fête-1-01",
                routeID = OneStopRouteID("r-f2565-115"),
                direction = "Terminus Vaudreuil",
                version = VERSION,
                dates = listOf(
                    LocalDate.of(2022, 12, 26),
                    LocalDate.of(2023, 1, 2)
                )
            )
        )
        /*assertThat(trips).contains(
            ScheduledTrip(
                _id = "3282393-PI-A22-PI_GTFS-Samedi-01",
                routeID = OneStopRouteID("r-f2565-115"),
                direction = "Terminus Vaudreuil",
                version = VERSION,
                dates = listOf(
                    LocalDate.of(2022, 11, 26),
                    LocalDate.of(2022, 12, 3),
                    LocalDate.of(2022, 12, 10),
                    LocalDate.of(2022, 12, 17),
                    LocalDate.of(2022, 12, 24),
                    LocalDate.of(2022, 12, 31),
                    LocalDate.of(2023, 1, 7),
                )
            )
        )*/
        /*assertThat(trips).contains(
            ScheduledTrip(
                _id = "3282456-PI-A22-PI_GTFS-Dimanche-01",
                routeID = OneStopRouteID("r-f2565-115"),
                direction = "Terminus Vaudreuil",
                version = VERSION,
                dates = listOf(
                    LocalDate.of(2022, 11, 27),
                    LocalDate.of(2022, 12, 4),
                    LocalDate.of(2022, 12, 11),
                    LocalDate.of(2022, 12, 18),
                    LocalDate.of(2022, 12, 25),
                    LocalDate.of(2023, 1, 1),
                    LocalDate.of(2023, 1, 8),
                )
            )
        )*/
    }
}
