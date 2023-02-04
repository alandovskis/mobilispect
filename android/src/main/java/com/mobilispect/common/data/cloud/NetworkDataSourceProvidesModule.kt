package com.mobilispect.common.data.cloud

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.okhttp.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkDataSourceProvidesModule {
    @Provides
    fun networkDataSource(): MobilispectAPINetworkDataSource {
        @Suppress("EmptyFunctionBlock")
        val trustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> =
                emptyArray()
        }

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(trustManager), SecureRandom())

        val hostnameVerifier = HostnameVerifier { _, _ -> true }

        val config = OkHttpConfig()
        config.config {
            sslSocketFactory(sslContext.socketFactory, trustManager)
            hostnameVerifier(hostnameVerifier)
        }

        return MobilispectAPINetworkDataSource(
            httpEngine = OkHttpEngine(
                config
            )
        )
    }
}
