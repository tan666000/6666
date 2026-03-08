// AIViewModel.kt
package com.steadywj.wjfakelocation.manager.ui.ai.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadywj.wjfakelocation.domain.service.AIAction
import com.steadywj.wjfakelocation.domain.service.AIService
import com.steadywj.wjfakelocation.domain.service.AutomationRule
import com.steadywj.wjfakelocation.domain.service.LocationPrediction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AI 功能 ViewModel
 */
@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiService: AIService
) : ViewModel() {
    
    /** UI 状态 */
    private val _uiState = MutableStateFlow(AIUiState())
    val uiState: StateFlow<AIUiState> = _uiState.asStateFlow()
    
    /** 预测列表 */
    private val _predictions = MutableStateFlow<List<LocationPrediction>>(emptyList())
    val predictions: StateFlow<List<LocationPrediction>> = _predictions.asStateFlow()
    
    /** 自动化规则列表 */
    private val _autoRules = MutableStateFlow<List<AutomationRule>>(emptyList())
    val autoRules: StateFlow<List<AutomationRule>> = _autoRules.asStateFlow()
    
    init {
        // 监听用户模式变化
        viewModelScope.launch {
            aiService.userPatterns.collect { patterns ->
                _autoRules.value = patterns.autoRules
            }
        }
        
        // 监听预测变化
        viewModelScope.launch {
            aiService.predictions.collect { predictions ->
                _predictions.value = predictions
            }
        }
    }
    
    /**
     * 更新预测
     */
    fun updatePredictions(currentLocation: Location?) {
        viewModelScope.launch {
            if (currentLocation != null) {
                val predictions = aiService.predictDestination(
                    currentLocation = currentLocation,
                    currentTime = System.currentTimeMillis()
                )
                _predictions.value = predictions
            }
        }
    }
    
    /**
     * 添加自动化规则
     */
    fun addAutoRule(locationName: String, action: AIAction) {
        viewModelScope.launch {
            val rule = AutomationRule(
                locationName = locationName,
                action = when(action) {
                    AIAction.ENABLE_FAKE_LOCATION -> com.steadywj.wjfakelocation.domain.service.AutomationAction.ENABLE_FAKE_LOCATION
                    AIAction.DISABLE_FAKE_LOCATION -> com.steadywj.wjfakelocation.domain.service.AutomationAction.DISABLE_FAKE_LOCATION
                    AIAction.SWITCH_PROFILE -> com.steadywj.wjfakelocation.domain.service.AutomationAction.SWITCH_PROFILE
                    AIAction.NOTIFY_USER -> com.steadywj.wjfakelocation.domain.service.AutomationAction.NOTIFY_USER
                },
                enabled = true
            )
            aiService.addAutoRule(rule)
            _uiState.value = _uiState.value.copy(showSuccessMessage = "自动化规则已添加")
        }
    }
    
    /**
     * 检查自动触发
     */
    fun checkAutoTrigger(currentLocation: Location) {
        viewModelScope.launch {
            val result = aiService.checkAutoTrigger(currentLocation)
            result?.let { trigger ->
                _uiState.value = _uiState.value.copy(
                    showAutoTriggerDialog = true,
                    autoTriggerResult = trigger
                )
            }
        }
    }
    
    /**
     * 清除学习数据
     */
    fun clearLearningData() {
        viewModelScope.launch {
            aiService.clearLearningData()
            _uiState.value = _uiState.value.copy(
                showSuccessMessage = "学习数据已清除"
            )
        }
    }
    
    /**
     * 清除消息
     */
    fun clearMessage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(showSuccessMessage = null)
        }
    }
}

/**
 * AI 动作枚举
 */
enum class AIAction {
    ENABLE_FAKE_LOCATION,
    DISABLE_FAKE_LOCATION,
    SWITCH_PROFILE,
    NOTIFY_USER
}

/**
 * AI UI 状态
 */
data class AIUiState(
    val showSuccessMessage: String? = null,
    val showAutoTriggerDialog: Boolean = false,
    val autoTriggerResult: com.steadywj.wjfakelocation.domain.service.AutoTriggerResult? = null,
    val isLoading: Boolean = false
)
