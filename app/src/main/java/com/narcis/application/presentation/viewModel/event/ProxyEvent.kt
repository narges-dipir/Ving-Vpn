package com.narcis.application.presentation.viewModel.event

import android.content.Context
import com.narcis.application.presentation.viewModel.model.DefaultConfig

sealed class ProxyEvent {
    data class ConfigEvent(val defaultConfig: DefaultConfig, val current: Context) : ProxyEvent()

    object triggerRefresh: ProxyEvent()
}
