#!/usr/bin/env bash -euo
cat > src/main/kotlin/com/mobilispect/backend/data/transit_land/TransitLandCredentialsConfiguration.kt < EOF
package com.mobilispect.backend.data.transit_land

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TransitLandCredentialsConfiguration {
@Bean
    fun repository(): TransitLandCredentialsRepository = DummyTransitLandCredentialsRepository()
}
EOF

cat > src/main/kotlin/com/mobilispect/backend/data/transit_land/DummyTransitLandCredentialsRepository.kt < EOF
package com.mobilispect.backend.data.transit_land

class DummyTransitLandCredentialsRepository : TransitLandCredentialsRepository {
    override fun get(): String = "CvaXEwfuCJ7x1mC169r53ygiVU8N55mj"
}
EOF
