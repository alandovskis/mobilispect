package com.mobilispect.backend.data.transit_land.internal.service

import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.transit_land.api.AgencyResult
import com.mobilispect.backend.data.transit_land.api.RouteResult
import com.mobilispect.backend.data.transit_land.api.StopResult
import com.mobilispect.backend.data.transit_land.api.TransitLandAPI
import com.mobilispect.backend.data.transit_land.internal.client.TransitLandClient
import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * An instance of [TransitLandAPI] that is rate limited and has retries.
 */
@Service
@Primary
class RateLimitingRetryingTransitLandAPIService(
    private val transitLandClient: TransitLandClient,
) : TransitLandAPI {
    private val logger = LoggerFactory.getLogger(TransitLandAPI::class.java)
    private lateinit var perMinuteRateLimiter: RateLimiter
    private lateinit var perMonthRateLimiter: RateLimiter
    private lateinit var retry: Retry

    init {
        val perMinuteRateLimitConfig = RateLimiterConfig.custom()
            .limitForPeriod(60)
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .timeoutDuration(Duration.ofSeconds(1))
            .build()
        val perMonthRateLimitConfig = RateLimiterConfig.custom()
            .limitForPeriod(10_000)
            .limitRefreshPeriod(Duration.ofHours(720))
            .timeoutDuration(Duration.ofSeconds(1))
            .build()
        val rateLimitRegistry = RateLimiterRegistry.ofDefaults()
        perMinuteRateLimiter = rateLimitRegistry.rateLimiter("transitLandAPIPerMin", perMinuteRateLimitConfig)
        perMonthRateLimiter = rateLimitRegistry.rateLimiter("transitLandAPIPerMonth", perMonthRateLimitConfig)

        val retryConfig = RetryConfig.custom<Int>()
            .maxAttempts(5)
            .intervalFunction(IntervalFunction.ofExponentialBackoff(1000, 1.5))
            .build()
        val retryRegistry = RetryRegistry.ofDefaults()
        retry = retryRegistry.retry("transitLandRetry", retryConfig)
        retry.eventPublisher.onRetry {
            logger.trace(
                "Retry ${it.numberOfRetryAttempts} of ${retry.retryConfig.maxAttempts}",
            )
        }
    }

    override fun feed(apiKey: String, feedID: String): Result<VersionedFeed> {
        return retry {
            rateLimit(perMonthRateLimiter) {
                rateLimit(perMinuteRateLimiter) {
                    transitLandClient.feed(apiKey = apiKey, feedID = feedID)
                }
            }

        }
    }


    override fun agencies(apiKey: String, region: String?, feedID: String?): Result<AgencyResult> {
        return retry {
            rateLimit(perMonthRateLimiter) {
                rateLimit(perMinuteRateLimiter) {
                    transitLandClient.agencies(apiKey = apiKey, region = region, feedID = feedID)
                }
            }
        }
    }

    override fun routes(apiKey: String, feedID: String, paging: PagingParameters): Result<RouteResult> {
        return retry {
            rateLimit(perMonthRateLimiter) {
                rateLimit(perMinuteRateLimiter) {
                    transitLandClient.routes(apiKey = apiKey, feedID = feedID)
                }
            }
        }
    }

    override fun stops(apiKey: String, feedID: String, paging: PagingParameters): Result<StopResult> {
        return retry {
            rateLimit(perMonthRateLimiter) {
                rateLimit(perMinuteRateLimiter) {
                    transitLandClient.stops(apiKey = apiKey, feedID = feedID)
                }
            }
        }
    }

    private fun <T> retry(function: () -> T): T = Retry.decorateSupplier(retry) { function() }.get()

    private fun <T> rateLimit(rateLimiter: RateLimiter, function: () -> T): T =
        RateLimiter.decorateSupplier(rateLimiter) { function() }.get()
}