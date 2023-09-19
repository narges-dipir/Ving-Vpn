package com.abrnoc.application.presentation.viewModel.event

sealed class ProxyEvent {
    data class EmailQuery(val proxyEvent: ProxyEvent): ProxyEvent()
}