// SearchHistoryManager.kt
package com.steadywj.wjfakelocation.manager.search.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 搜索历史管理�?
 * 
 * 功能:
 * - 历史记录存储
 * - 热门搜索推荐
 * - 智能排序（频�?+ 时间�?
 */
@Singleton
class SearchHistoryManager @Inject constructor(
    private val context: Context
) {
    
    /** 搜索历史列表 */
    private val _searchHistory = MutableStateFlow<List<SearchRecord>>(emptyList())
    val searchHistory: Flow<List<SearchRecord>> = _searchHistory.asStateFlow()
    
    /** 热门搜索列表 */
    private val _hotSearches = MutableStateFlow<List<String>>(emptyList())
    val hotSearches: Flow<List<String>> = _hotSearches.asStateFlow()
    
    /** 历史记录文件 */
    private val historyFile: File by lazy {
        File(context.filesDir, "search_history.json")
    }
    
    /** 最大历史记录数 */
    private val MAX_HISTORY_SIZE = 50
    
    init {
        loadHistory()
        updateHotSearches()
    }
    
    /**
     * 添加搜索记录
     */
    suspend fun addSearchRecord(query: String, category: String = "default") {
        return withContext(Dispatchers.IO) {
            val currentList = _searchHistory.value.toMutableList()
            
            // 查找是否已存在相同查�?
            val existingIndex = currentList.indexOfFirst { it.query == query }
            
            if (existingIndex >= 0) {
                // 已存在，更新计数和时�?
                val existing = currentList[existingIndex]
                currentList[existingIndex] = existing.copy(
                    count = existing.count + 1,
                    lastSearchedAt = System.currentTimeMillis()
                )
                // 移到最前面
                val record = currentList.removeAt(existingIndex)
                currentList.add(0, record)
            } else {
                // 新增记录
                val newRecord = SearchRecord(
                    query = query,
                    category = category,
                    count = 1,
                    lastSearchedAt = System.currentTimeMillis()
                )
                currentList.add(0, newRecord)
                
                // 限制大小
                while (currentList.size > MAX_HISTORY_SIZE) {
                    currentList.removeAt(currentList.size - 1)
                }
            }
            
            _searchHistory.value = currentList
            
            // 异步保存到文�?
            saveHistoryAsync()
            
            // 更新热门搜索
            updateHotSearches()
        }
    }
    
    /**
     * 删除单条记录
     */
    suspend fun deleteRecord(record: SearchRecord) {
        return withContext(Dispatchers.IO) {
            _searchHistory.value = _searchHistory.value.filter { it != record }
            saveHistoryAsync()
            updateHotSearches()
        }
    }
    
    /**
     * 清除所有历史记�?
     */
    suspend fun clearAllHistory() {
        return withContext(Dispatchers.IO) {
            _searchHistory.value = emptyList()
            _hotSearches.value = emptyList()
            
            withContext(Dispatchers.IO) {
                historyFile.delete()
            }
        }
    }
    
    /**
     * 获取推荐搜索（基于时间和频率�?
     */
    fun getRecommendations(limit: Int = 5): List<SearchRecord> {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        
        return _searchHistory.value
            .map { record ->
                val recencyScore = calculateRecencyScore(record.lastSearchedAt, now, oneDayMillis)
                val frequencyScore = Math.log(record.count.toDouble() + 1)
                val score = recencyScore * 0.6 + frequencyScore * 0.4 // 60% 时效�?+ 40% 频率
                
                record to score
            }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }
    
    /**
     * 按分类筛选历史记�?
     */
    fun filterByCategory(category: String): List<SearchRecord> {
        return if (category == "all") {
            _searchHistory.value
        } else {
            _searchHistory.value.filter { it.category == category }
        }
    }
    
    /**
     * 导出历史记录
     */
    suspend fun exportHistory(): String {
        return withContext(Dispatchers.IO) {
            // 简�?JSON 格式导出
            buildString {
                appendLine("[")
                _searchHistory.value.forEachIndexed { index, record ->
                    appendLine("  {")
                    appendLine("    \"query\": \"${record.query}\",")
                    appendLine("    \"category\": \"${record.category}\",")
                    appendLine("    \"count\": ${record.count},")
                    appendLine("    \"lastSearchedAt\": ${record.lastSearchedAt}")
                    appendLine("  }${if (index < _searchHistory.value.size - 1) "," else ""}")
                }
                appendLine("]")
            }
        }
    }
    
    // ==================== 内部方法 ====================
    
    /**
     * 加载历史记录
     */
    private fun loadHistory() {
        try {
            if (historyFile.exists()) {
                val content = historyFile.readText()
                // 使用 JSON 解析库（�?Kotlinx Serialization�?
                // TODO: 添加 kotlinx-serialization 依赖并实现完整解�?
                // 这里简化处理，实际应该解析 JSON
                _searchHistory.value = emptyList() // 占位实现
            }
        } catch (e: Exception) {
            _searchHistory.value = emptyList()
        }
    }
    
    /**
     * 异步保存历史记录
     */
    private fun saveHistoryAsync() {
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            try {
                // 使用 JSON 序列�?
                // TODO: 添加 kotlinx-serialization 依赖并实现完整序列化
                val json = buildString {
                    appendLine("[")
                    _searchHistory.value.forEachIndexed { index, record ->
                        appendLine("  {")
                        appendLine("    \"query\": \"${record.query}\",")
                        appendLine("    \"category\": \"${record.category}\",")
                        appendLine("    \"count\": ${record.count},")
                        appendLine("    \"lastSearchedAt\": ${record.lastSearchedAt}")
                        appendLine("  }${if (index < _searchHistory.value.size - 1) "," else ""}")
                    }
                    appendLine("]")
                }
                historyFile.writeText(json)
            } catch (e: Exception) {
                // 忽略保存失败
            }
        }
    }
    
    /**
     * 更新热门搜索列表
     */
    private fun updateHotSearches() {
        val hotList = _searchHistory.value
            .filter { it.count >= 3 } // 至少搜索 3 �?
            .sortedByDescending { it.count }
            .take(10)
            .map { it.query }
        
        _hotSearches.value = hotList
    }
    
    /**
     * 计算时效性分�?
     */
    private fun calculateRecencyScore(lastSearchedAt: Long, now: Long, oneDayMillis: Long): Double {
        val hoursAgo = (now - lastSearchedAt).toDouble() / oneDayMillis * 24
        
        return when {
            hoursAgo < 1 -> 1.0 // 1 小时�?
            hoursAgo < 24 -> 0.8 // 1 天内
            hoursAgo < 168 -> 0.6 // 1 周内
            hoursAgo < 720 -> 0.4 // 1 月内
            else -> 0.2 // 更早
        }
    }
}

// ==================== 数据模型 ====================

/**
 * 搜索记录
 */
data class SearchRecord(
    val query: String,
    val category: String = "default",
    val count: Int = 1,
    val lastSearchedAt: Long = System.currentTimeMillis()
)

/**
 * POI 分类
 */
enum class POICategory(val displayName: String) {
    FOOD("美食"),
    HOTEL("酒店"),
    SHOPPING("购物"),
    TRANSPORT("交�?),
    EDUCATION("教育"),
    MEDICAL("医疗"),
    ENTERTAINMENT("娱乐"),
    DEFAULT("其他")
}

/**
 * 热门搜索�?
 */
data class HotSearchItem(
    val query: String,
    val trend: SearchTrend, // 上升、下降、稳�?
    val count: Int
)

/**
 * 搜索趋势
 */
enum class SearchTrend {
    RISING,    // 上升
    FALLING,   // 下降
    STABLE     // 稳定
}
