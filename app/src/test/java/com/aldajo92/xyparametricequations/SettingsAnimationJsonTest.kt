package com.aldajo92.xyparametricequations

import com.aldajo92.xyparametricequations.domain.SettingsAnimation
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsAnimationJsonTest {

    private val jsonBuilder = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter = jsonBuilder.adapter(SettingsAnimation::class.java)

    @Test
    fun `converting SettingsAnimation Object to JSON`() {
        val expected =
            "{\"tMin\":-10.0,\"tMax\":10.0,\"timeDurationMillis\":5000,\"showPath\":true,\"pathPoints\":20}"
        val settingsAnimation = SettingsAnimation(
            tMin = -10f,
            tMax = 10f,
            timeDurationMillis = 5000,
            showPath = true,
            pathPoints = 20
        )

        val json = jsonAdapter.toJson(settingsAnimation)

        assertEquals(expected, json)
    }

    @Test
    fun `converting SettingsAnimation JSON to Object`() {
        val json =
            "{\"tMin\":-10.0,\"tMax\":10.0,\"timeDurationMillis\":5000,\"showPath\":false,\"pathPoints\":20}"
        val expected = SettingsAnimation(
            tMin = -10f,
            tMax = 10f,
            timeDurationMillis = 5000,
            showPath = false,
            pathPoints = 20
        )

        val settingsAnimation = jsonAdapter.fromJson(json)

        assertEquals(expected, settingsAnimation)
    }

    @Test
    fun `converting SettingsAnimation JSON to Object with Missing Parameter`() {
        val json =
            "{\"tMin\":-10.0,\"tMax\":10.0,\"timeDurationMillis\":5000}"
        val expected = SettingsAnimation(
            tMin = -10f,
            tMax = 10f,
            timeDurationMillis = 5000,
            showPath = true,
            pathPoints = 20
        )

        val settingsAnimation = jsonAdapter.fromJson(json)

        assertEquals(expected, settingsAnimation)
    }

}
