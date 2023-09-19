package com.abrnoc.application.presentation.viewModel.state

import com.abrnoc.application.repository.model.DefaultConfig

data class DefaultConfigState(
    val isLoading: Boolean = false,
    val configs: List<DefaultConfig>? = emptyList(),
    val error: String = "",
)
