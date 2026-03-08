// WJFakeLocationApplication.kt
package com.steadywj.wjfakelocation

import android.app.Application
import com.amap.api.maps2d.AMapInterface
import com.amap.api.services.core.ServiceSettings
import com.baidu.mapapi.SDKInitializer
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用全局上下文
 * 负责初始化第三方 SDK 和全局配置
 */
@HiltAndroidApp
class WJFakeLocationApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化高德地图 SDK（预加载以提升性能）
        initAMapSDK()
        
        // 初始化百度地图 SDK（多地图引擎支持）
        initBaiduMapSDK()
    }
    
    /**
     * 初始化高德地图 SDK
     * 提前初始化可避免首次使用时的冷启动延迟
     */
    private fun initAMapSDK() {
        try {
            // 初始化地图服务设置
            ServiceSettings.getInstance(this)
            
            // 预加载地图接口（可选，根据实际需求）
            AMapInterface.getInstance(this)
            
            // 注意：API Key 需要在 AndroidManifest.xml 中配置
            // <meta-data
            //     android:name="com.amap.api.v2.apikey"
            //     android:value="你的_API_KEY"/>
        } catch (e: Exception) {
            // 忽略初始化异常，SDK 会在首次使用时自动初始化
            // 可以在这里添加日志记录
        }
    }
    
    /**
     * 初始化百度地图 SDK
     * 支持多地图引擎切换
     */
    private fun initBaiduMapSDK() {
        try {
            // 初始化百度地图 SDK
            SDKInitializer.initialize(this)
            
            // 注意：API Key 需要在 AndroidManifest.xml 中配置
            // <meta-data
            //     android:name="com.baidu.lbsyun.API_KEY"
            //     android:value="你的_API_KEY"/>
        } catch (e: Exception) {
            // 忽略初始化异常
        }
    }
}
