package com.mobilispect.backend

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ModulithArchitectureTest {
    @Test
    fun verifiesArchitecture() {
        ApplicationModules.of(ApiApplication::class.java).verify()
    }
}