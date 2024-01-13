package com.narcis.application.presentation.viewModel.state

import com.narcis.application.presentation.viewModel.model.DefaultConfig

data class DefaultConfigState(
    val isLoading: Boolean = false,
    val configs: List<DefaultConfig>? = emptyList(),
    val error: String = "",
    val isRefreshing: Boolean = false,
)
