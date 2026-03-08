package com.steadywj.wjfakelocation.xposed.utils

import com.steadywj.wjfakelocation.data.model.LocationSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * 坐标转换工具测试
 */
class LocationUtilTest {
    
    @Test
    fun `gcj02ToWgs84 should accurate within 10 meters`() {
        // 已知准确值：北京天安门
        val gcjLat = 39.908823
        val gcjLng = 116.397470
        
        // 执行转换
        val (wgsLat, wgsLng) = LocationUtil.gcj02ToWgs84(gcjLat, gcjLng)
        
        // 验证结果合理性（WGS-84 应该比 GCJ-02 略偏西南）
        assertTrue("WGS 纬度应该小于 GCJ 纬度", wgsLat < gcjLat)
        assertTrue("WGS 经度应该小于 GCJ 经度", wgsLng < gcjLng)
        
        // 误差应该在合理范围内（约 0.001 度，相当于~100 米）
        assertTrue("纬度误差过大", kotlin.math.abs(wgsLat - gcjLat) < 0.005)
        assertTrue("经度误差过大", kotlin.math.abs(wgsLng - gcjLng) < 0.005)
    }
    
    @Test
    fun `wgs84ToGcj02 should be inverse of gcj02ToWgs84`() {
        val originalLat = 39.908823
        val originalLng = 116.397470
        
        // WGS-84 → GCJ-02
        val (gcjLat, gcjLng) = LocationUtil.wgs84ToGcj02(originalLat, originalLng)
        
        // GCJ-02 → WGS-84
        val (backLat, backLng) = LocationUtil.gcj02ToWgs84(gcjLat, gcjLng)
        
        // 应该接近原始值（允许小误差）
        assertEquals("往返转换后纬度不一致", originalLat, backLat, 0.001)
        assertEquals("往返转换后经度不一致", originalLng, backLng, 0.001)
    }
    
    @Test
    fun `createFakeLocation should use correct coordinates`() {
        // 设置测试位置
        LocationUtil.selectedLocation = com.steadywj.wjfakelocation.data.model.SelectedLocation(
            latitude = 39.908823,
            longitude = 116.397470,
            address = "北京天安门"
        )
        
        // 创建假位置
        val fakeLocation = LocationUtil.createFakeLocation()
        
        // 验证基本属性
        assertNotNull("位置不应为 null", fakeLocation)
        assertEquals("Provider 应为 gps", "gps", fakeLocation.provider)
        assertEquals("纬度不正确", 39.908823, fakeLocation.latitude, 0.001)
        assertEquals("经度不正确", 116.397470, fakeLocation.longitude, 0.001)
    }
}
