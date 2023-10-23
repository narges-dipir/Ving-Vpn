package com.abrnoc.application.presentation.viewModel.event

sealed class SendCodeEvent{
    data class EmailQuery(val email: String): SendCodeEvent()
    object ClearEvent: SendCodeEvent()
}
