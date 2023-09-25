package com.abrnoc.application.presentation.viewModel.event

import android.content.Context
import com.abrnoc.application.repository.model.DefaultConfig

sealed class ProxyEvent {
    data class ConfigEvent(val defaultConfig: DefaultConfig, val current: Context) : ProxyEvent()
}
