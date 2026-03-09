// SettingsViewModel.kt
package com.steadywj.wjfakelocation.manager.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadywj.wjfakelocation.data.model.LocationSettings
import com.steadywj.wjfakelocation.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 设置 ViewModel
 * 管理设置界面的状态和业务逻辑
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    /** 当前设置（响应式数据流） */
    val settings: StateFlow<LocationSettings> = preferencesRepository.settings

    /** UI 状�?*/
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /**
     * 更新精度设置
     * @param enabled 是否启用
     * @param value 精度值（米）
     */
    fun updateAccuracy(enabled: Boolean, value: Double) {
        viewModelScope.launch {
            val current = settings.value
            preferencesRepository.updateSettings(
                current.copy(
                    useAccuracy = enabled,
                    accuracy = value
                )
            )
        }
    }

    /**
     * 更新海拔设置
     * @param enabled 是否启用
     * @param value 海拔值（米）
     */
    fun updateAltitude(enabled: Boolean, value: Double) {
        viewModelScope.launch {
            val current = settings.value
            preferencesRepository.updateSettings(
                current.copy(
                    useAltitude = enabled,
                    altitude = value
                )
            )
        }
    }

    /**
     * 更新随机偏移设置
     * @param enabled 是否启用
     * @param radius 偏移半径（米�?
     */
    fun updateRandomize(enabled: Boolean, radius: Double) {
        viewModelScope.launch {
            val current = settings.value
            preferencesRepository.updateSettings(
                current.copy(
                    useRandomize = enabled,
                    randomizeRadius = radius
                )
            )
        }
    }

    /**
     * 更新速度设置
     * @param enabled 是否启用
     * @param value 速度值（�?秒）
     */
    fun updateSpeed(enabled: Boolean, value: Float) {
        viewModelScope.launch {
            val current = settings.value
            preferencesRepository.updateSettings(
                current.copy(
                    useSpeed = enabled,
                    speed = value
                )
            )
        }
    }

    /**
     * 保存 API Key
     * @param apiKey API Key
     */
    fun saveApiKey(apiKey: String) {
        viewModelScope.launch {
            preferencesRepository.saveApiKey(apiKey)
            _uiState.value = _uiState.value.copy(showSuccessMessage = "API Key 已保�?)
        }
    }

    /**
     * 清除 API Key
     */
    fun clearApiKey() {
        viewModelScope.launch {
            preferencesRepository.clearApiKey()
            _uiState.value = _uiState.value.copy(showSuccessMessage = "API Key 已清�?)
        }
    }

    /**
     * 保存情景模式
     * @param name 模式名称
     */
    fun saveProfile(name: String) {
        viewModelScope.launch {
            preferencesRepository.saveProfile(name, settings.value)
            _uiState.value = _uiState.value.copy(showSuccessMessage = "情景模式已保存：$name")
        }
    }

    /**
     * 加载情景模式
     * @param name 模式名称
     */
    fun loadProfile(name: String) {
        viewModelScope.launch {
            val profile = preferencesRepository.loadProfile(name)
            profile?.let {
                preferencesRepository.updateSettings(it)
                _uiState.value = _uiState.value.copy(showSuccessMessage = "情景模式已加载：$name")
            }
        }
    }

    /**
     * 清除消息提示
     */
    fun clearMessage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(showSuccessMessage = null)
        }
    }
}

/**
 * 设置 UI 状�?
 * @property showSuccessMessage 成功消息
 * @property showApiKeyDialog 显示 API Key 对话�?
 * @property showProfileDialog 显示情景模式对话�?
 */
data class SettingsUiState(
    val showSuccessMessage: String? = null,
    val showApiKeyDialog: Boolean = false,
    val showProfileDialog: Boolean = false
)
