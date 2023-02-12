package com.mobilispect.benchmark.frequency_commitment

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.frequencyCommitmentWaitForContent() {
    // Wait until content is loaded
    device.wait(Until.hasObject(By.text("ON Weekdays")), 30_000)
}