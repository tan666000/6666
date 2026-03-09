pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Google 仓库（AndroidX、Support Library 等）
        google()
        
        // Maven Central（通用依赖库）
        mavenCentral()
        
        // Xposed 仓库
        maven {
            url = uri("https://api.xposed.info/")
            content {
                includeGroup("de.robv.android.xposed")
            }
        }
        
        // 高德地图仓库 - 官方 Maven 源
        maven {
            name = "AMAP"
            url = uri("https://maven.amap.com/maven2/")
            content {
                includeGroup("com.amap.api")
            }
        }
        
        // 百度地图仓库 - 官方 Maven 源（通过 JitPack 镜像）
        maven {
            name = "BaiduLBS"
            url = uri("https://jitpack.io")
            content {
                includeGroup("com.github.baidu.lbsyun")
            }
        }
        
        // 备用仓库 - 华为 Maven（部分国内 SDK）
        maven {
            name = "Huawei"
            url = uri("https://developer.huawei.com/repo/HMSKit")
        }
    }
}

rootProject.name = "WJFakeLocation"
include(":app")
