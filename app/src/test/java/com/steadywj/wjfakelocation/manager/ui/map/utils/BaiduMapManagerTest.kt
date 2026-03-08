package com.steadywj.wjfakelocation.domain.service

import org.junit.Assert.*
import org.junit.Test

/**
 * 坐标转换工具测试（百度地图）
 */
class BaiduMapManagerTest {
    
    @Test
    fun `bd09ToGcj02 should convert correctly`() {
        // Given: 百度地图坐标（北京）
        val bdLat = 39.915
        val bdLng = 116.404
        
        // When
        val (gcjLat, gcjLng) = createBaiduMapManager().bd09ToGcj02(bdLat, bdLng)
        
        // Then: GCJ-02 应该比 BD-09 略小
        assertTrue("GCJ 纬度应该小于百度纬度", gcjLat < bdLat)
        assertTrue("GCJ 经度应该小于百度经度", gcjLng < bdLng)
        
        // 误差应该在合理范围内
        assertTrue("纬度误差过大", kotlin.math.abs(bdLat - gcjLat) < 0.01)
        assertTrue("经度误差过大", kotlin.math.abs(bdLng - gcjLng) < 0.01)
    }
    
    @Test
    fun `gcj02ToBd09 should convert correctly`() {
        // Given: GCJ-02 坐标
        val gcjLat = 39.908823
        val gcjLng = 116.397470
        
        // When
        val (bdLat, bdLng) = createBaiduMapManager().gcj02ToBd09(gcjLat, gcjLng)
        
        // Then: BD-09 应该比 GCJ-02 略大
        assertTrue("百度纬度应该大于 GCJ 纬度", bdLat > gcjLat)
        assertTrue("百度经度应该大于 GCJ 经度", bdLng > gcjLng)
    }
    
    @Test
    fun `bd09ToWgs84 should be inverse of wgs84ToBd09`() {
        // Given: WGS-84 原始坐标
        val originalLat = 39.908823
        val originalLng = 116.397470
        
        // When: WGS-84 → BD-09 → WGS-84
        val manager = createBaiduMapManager()
        val (bdLat, bdLng) = manager.wgs84ToBd09(originalLat, originalLng)
        val (backLat, backLng) = manager.bd09ToWgs84(bdLat, bdLng)
        
        // Then: 往返转换应该接近原始值
        assertEquals("往返转换后纬度不一致", originalLat, backLat, 0.001)
        assertEquals("往返转换后经度不一致", originalLng, backLng, 0.001)
    }
    
    @Test
    fun `all coordinate conversions should be consistent`() {
        val manager = createBaiduMapManager()
        
        // 测试多个坐标点
        val testPoints = listOf(
            39.908823 to 116.397470, // 北京
            31.230416 to 121.473701, // 上海
            23.12911 to 113.264385,   // 广州
            30.572177 to 104.066451   // 成都
        )
        
        testPoints.forEach { (lat, lng) ->
            // WGS-84 → GCJ-02 → BD-09 → WGS-84
            val (gcjLat, gcjLng) = manager.wgs84ToGcj02(lat, lng)
            val (bdLat, bdLng) = manager.gcj02ToBd09(gcjLat, gcjLng)
            val (backLat, backLng) = manager.bd09ToWgs84(bdLat, bdLng)
            
            // 验证一致性
            assertEquals("坐标 ($lat, $lng) 转换不一致", lat, backLat, 0.002)
            assertEquals("坐标 ($lat, $lng) 转换不一致", lng, backLng, 0.002)
        }
    }
    
    private fun createBaiduMapManager(): BaiduMapManager {
        // 由于 BaiduMapManager 需要 Context，这里创建一个简化版本用于测试
        return object : BaiduMapManager(null as Any?) {
            override fun geocodeAddress(address: String): kotlinx.coroutines.flow.Flow<Result<Pair<Double, Double>>> {
                TODO("Not implemented for testing")
            }
            
            override fun reverseGeocode(latitude: Double, longitude: Double): kotlinx.coroutines.flow.Flow<Result<String>> {
                TODO("Not implemented for testing")
            }
        }
    }
}
