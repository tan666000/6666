// LocationUtilTest.kt
package com.steadywj.wjfakelocation.xposed.common

import android.location.Location
import org.junit.Assert.*
import org.junit.Test

/**
 * LocationUtil 单元测试
 * 测试定位工具类的功
 */
class LocationUtilTest {

    @Test
    fun `test createFakeLocation creates valid location`() {
        // When
        val fakeLocation = LocationUtil.createFakeLocation()

        // Then
        assertNotNull(fakeLocation)
        assertEquals("gps", fakeLocation.provider)
        assertTrue(fakeLocation.latitude in -90.0..90.0)
        assertTrue(fakeLocation.longitude in -180.0..180.0)
        assertTrue(fakeLocation.accuracy >= 0f)
    }

    @Test
    fun `test createFakeLocation with custom accuracy`() {
        // Given
        val customAccuracy = 50.0

        // When
        val fakeLocation = LocationUtil.createFakeLocation(accuracy = customAccuracy)

        // Then
        assertNotNull(fakeLocation)
        assertEquals(customAccuracy.toFloat(), fakeLocation.accuracy, 0.01f)
    }

    @Test
    fun `test calculateDistance calculates correct distance`() {
        // Given - 两个已知点（北京和上海）
        val beijingLat = 39.9042
        val beijingLon = 116.4074
        val shanghaiLat = 31.2304
        val shanghaiLon = 121.4737

        // When
        val distance = LocationUtil.calculateDistance(
            beijingLat, beijingLon,
            shanghaiLat, shanghaiLon
        )

        // Then - 实际距离约 1068km，允许 10% 误差
        assertTrue(distance > 1000000) // 大于 1000km
        assertTrue(distance < 1200000) // 小于 1200km
    }

    @Test
    fun `test calculateDistance same location returns zero`() {
        // Given
        val lat = 39.9042
        val lon = 116.4074

        // When
        val distance = LocationUtil.calculateDistance(lat, lon, lat, lon)

        // Then
        assertEquals(0.0, distance, 0.01)
    }

    @Test
    fun `test offsetLocation adds offset correctly`() {
        // Given
        val originalLocation = Location("gps").apply {
            latitude = 39.9042
            longitude = 116.4074
        }
        val offsetMeters = 100.0 // 100 米偏移

        // When
        val offsetLocation = LocationUtil.offsetLocation(originalLocation, offsetMeters, 45.0)

        // Then
        assertNotNull(offsetLocation)
        assertNotEquals(originalLocation.latitude, offsetLocation.latitude, 0.0)
        assertNotEquals(originalLocation.longitude, offsetLocation.longitude, 0.0)
        
        // 验证偏移量在合理范围内（100 米大约 0.001 度）
        val latDiff = kotlin.math.abs(offsetLocation.latitude - originalLocation.latitude)
        val lonDiff = kotlin.math.abs(offsetLocation.longitude - originalLocation.longitude)
        assertTrue(latDiff < 0.01) // 纬度差小于 0.01 度
        assertTrue(lonDiff < 0.01) // 经度差小于 0.01 度
    }

    @Test
    fun `test isLocationValid validates correct location`() {
        // Given
        val validLocation = Location("gps").apply {
            latitude = 39.9042
            longitude = 116.4074
            accuracy = 10f
        }

        // When
        val isValid = LocationUtil.isLocationValid(validLocation)

        // Then
        assertTrue(isValid)
    }

    @Test
    fun `test isLocationValid rejects null location`() {
        // When
        val isValid = LocationUtil.isLocationValid(null)

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `test isLocationValid rejects invalid latitude`() {
        // Given
        val invalidLocation = Location("gps").apply {
            latitude = 100.0 // 无效纬度（超出 -90 到 90）
            longitude = 116.4074
        }

        // When
        val isValid = LocationUtil.isLocationValid(invalidLocation)

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `test isLocationValid rejects invalid longitude`() {
        // Given
        val invalidLocation = Location("gps").apply {
            latitude = 39.9042
            longitude = 200.0 // 无效经度（超出 -180 到 180）
        }

        // When
        val isValid = LocationUtil.isLocationValid(invalidLocation)

        // Then
        assertFalse(isValid)
    }
}
