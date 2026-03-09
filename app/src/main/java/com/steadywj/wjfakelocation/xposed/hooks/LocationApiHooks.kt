// LocationApiHooks.kt
package com.steadywj.wjfakelocation.xposed.hooks

import android.location.Location
import android.os.Build
import com.steadywj.wjfakelocation.xposed.common.LocationUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * 位置 API Hook �?- 适配 Android 13-16 (API 33-36)
 * 
 * 主要功能:
 * - Hook Location 类的 getter 方法
 * - Hook LocationManager 的位置获取方�?
 * - 支持多版�?Android 适配
 */
class LocationApiHooks(private val appLpparam: LoadPackageParam) {
    private val tag = "[LocationApiHooks]"

    fun initHooks() {
        hookLocationAPI()
        XposedBridge.log("$tag Hooks initialized for ${appLpparam.packageName}")
    }

    private fun hookLocationAPI() {
        hookLocation(appLpparam.classLoader)
        hookLocationManager(appLpparam.classLoader)
    }

    private fun hookLocation(classLoader: ClassLoader) {
        try {
            val locationClass = XposedHelpers.findClass("android.location.Location", classLoader)

            // Hook getLatitude()
            XposedHelpers.findAndHookMethod(
                locationClass,
                "getLatitude",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!shouldFakeLocation()) return
                        
                        LocationUtil.updateLocation()
                        XposedBridge.log("$tag Intercepting getLatitude()")
                        XposedBridge.log("\t Original: ${param.result as Double}")
                        param.result = LocationUtil.latitude
                        XposedBridge.log("\t Fake: ${LocationUtil.latitude}")
                    }
                }
            )

            // Hook getLongitude()
            XposedHelpers.findAndHookMethod(
                locationClass,
                "getLongitude",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!shouldFakeLocation()) return
                        
                        LocationUtil.updateLocation()
                        XposedBridge.log("$tag Intercepting getLongitude()")
                        XposedBridge.log("\t Original: ${param.result as Double}")
                        param.result = LocationUtil.longitude
                        XposedBridge.log("\t Fake: ${LocationUtil.longitude}")
                    }
                }
            )

            // Hook getAccuracy() - Android 13+
            XposedHelpers.findAndHookMethod(
                locationClass,
                "getAccuracy",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!shouldFakeLocation()) return
                        
                        LocationUtil.updateLocation()
                        XposedBridge.log("$tag Intercepting getAccuracy()")
                        XposedBridge.log("\t Original: ${param.result as Float}")
                        if (LocationUtil.useAccuracy) {
                            param.result = LocationUtil.accuracy
                            XposedBridge.log("\t Fake: ${LocationUtil.accuracy}")
                        }
                    }
                }
            )

            // Hook getAltitude()
            XposedHelpers.findAndHookMethod(
                locationClass,
                "getAltitude",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!shouldFakeLocation()) return
                        
                        LocationUtil.updateLocation()
                        XposedBridge.log("$tag Intercepting getAltitude()")
                        XposedBridge.log("\t Original: ${param.result as Double}")
                        if (LocationUtil.useAltitude) {
                            param.result = LocationUtil.altitude
                            XposedBridge.log("\t Fake: ${LocationUtil.altitude}")
                        }
                    }
                }
            )

            // Hook getVerticalAccuracyMeters() - Android 13+
            XposedHelpers.findAndHookMethod(
                locationClass,
                "getVerticalAccuracyMeters",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!shouldFakeLocation()) return
                        
                        LocationUtil.updateLocation()
                        XposedBridge.log("$tag Intercepting getVerticalAccuracyMeters()")
                        XposedBridge.log("\t Original: ${param.result as Float}")
                        if (LocationUtil.useVerticalAccuracy) {
                            param.result = LocationUtil.verticalAccuracy
                            XposedBridge.log("\t Fake: ${LocationUtil.verticalAccuracy}")
                        }
                    }
                }
            )

            // Hook getSpeed()
            XposedHelpers.findAndHookMethod(
                locationClass,
                "getSpeed",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!shouldFakeLocation()) return
                        
                        LocationUtil.updateLocation()
                        XposedBridge.log("$tag Intercepting getSpeed()")
                        XposedBridge.log("\t Original: ${param.result as Float}")
                        if (LocationUtil.useSpeed) {
                            param.result = LocationUtil.speed
                            XposedBridge.log("\t Fake: ${LocationUtil.speed}")
                        }
                    }
                }
            )

            // Hook getSpeedAccuracyMetersPerSecond() - Android 13+
            XposedHelpers.findAndHookMethod(
                locationClass,
                "getSpeedAccuracyMetersPerSecond",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!shouldFakeLocation()) return
                        
                        LocationUtil.updateLocation()
                        XposedBridge.log("$tag Intercepting getSpeedAccuracyMetersPerSecond()")
                        XposedBridge.log("\t Original: ${param.result as Float}")
                        if (LocationUtil.useSpeedAccuracy) {
                            param.result = LocationUtil.speedAccuracy
                            XposedBridge.log("\t Fake: ${LocationUtil.speedAccuracy}")
                        }
                    }
                }
            )

            // Android 12+ (API 31+) MSL 高度相关方法
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Hook getMslAltitudeMeters()
                XposedHelpers.findAndHookMethod(
                    locationClass,
                    "getMslAltitudeMeters",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (!shouldFakeLocation()) return
                            
                            LocationUtil.updateLocation()
                            XposedBridge.log("$tag Intercepting getMslAltitudeMeters()")
                            XposedBridge.log("\t Original: ${param.result as? Double}")
                            if (LocationUtil.useMeanSeaLevel) {
                                param.result = LocationUtil.meanSeaLevel
                                XposedBridge.log("\t Fake: ${LocationUtil.meanSeaLevel}")
                            }
                        }
                    }
                )

                // Hook getMslAltitudeAccuracyMeters()
                XposedHelpers.findAndHookMethod(
                    locationClass,
                    "getMslAltitudeAccuracyMeters",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (!shouldFakeLocation()) return
                            
                            LocationUtil.updateLocation()
                            XposedBridge.log("$tag Intercepting getMslAltitudeAccuracyMeters()")
                            XposedBridge.log("\t Original: ${param.result as? Float}")
                            if (LocationUtil.useMeanSeaLevelAccuracy) {
                                param.result = LocationUtil.meanSeaLevelAccuracy
                                XposedBridge.log("\t Fake: ${LocationUtil.meanSeaLevelAccuracy}")
                            }
                        }
                    }
                )
            }

        } catch (e: Exception) {
            XposedBridge.log("$tag Error hooking Location class: ${e.message}")
        }
    }

    private fun hookLocationManager(classLoader: ClassLoader) {
        try {
            val locationManagerClass = XposedHelpers.findClass(
                "android.location.LocationManager",
                classLoader
            )

            // Hook getLastKnownLocation(provider) - Android 13+
            XposedHelpers.findAndHookMethod(
                locationManagerClass,
                "getLastKnownLocation",
                String::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!shouldFakeLocation()) return
                        
                        XposedBridge.log("$tag Intercepting getLastKnownLocation(provider)")
                        val provider = param.args[0] as String
                        XposedBridge.log("\t Provider: $provider")
                        val fakeLocation = LocationUtil.createFakeLocation(provider = provider)
                        param.result = fakeLocation
                        XposedBridge.log("\t Fake location created: $fakeLocation")
                    }
                }
            )

            // Hook getLastKnownLocation(request) - Android 13+ (API 31+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                XposedHelpers.findAndHookMethod(
                    locationManagerClass,
                    "getLastKnownLocation",
                    "android.location.LocationRequest",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (!shouldFakeLocation()) return
                            
                            XposedBridge.log("$tag Intercepting getLastKnownLocation(request)")
                            val fakeLocation = LocationUtil.createFakeLocation()
                            param.result = fakeLocation
                            XposedBridge.log("\t Fake location created: $fakeLocation")
                        }
                    }
                )
            }

        } catch (e: Exception) {
            XposedBridge.log("$tag Error hooking LocationManager: ${e.message}")
        }
    }

    /**
     * 判断是否应该伪造位�?
     * 可以根据应用包名、用户设置等进行控制
     */
    private fun shouldFakeLocation(): Boolean {
        // TODO: 实现更精细的控制逻辑
        // - 检查是否在目标应用列表�?
        // - 检查用户是否启用了伪�?
        // - 检查情景模�?
        return true
    }
}
