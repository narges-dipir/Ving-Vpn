package com.abrnoc.application.presentation.viewModel.event

import com.abrnoc.application.repository.model.DefaultConfig

sealed class ProxyEvent {
    data class ConfigEvent(val defaultConfig: DefaultConfig) : ProxyEvent()
}
