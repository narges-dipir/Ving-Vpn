package com.abrnoc.application.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrnoc.application.presentation.viewModel.event.SendCodeEvent
import com.abrnoc.application.presentation.viewModel.state.SendCodeState
import com.abrnoc.domain.auth.SendVerificationCodeUseCase
import com.abrnoc.domain.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val sendCodeVerificationUseCase: SendVerificationCodeUseCase
) : ViewModel() {
    var state by mutableStateOf(SendCodeState())
    private var searchJob: Job? = null
    fun onEvent(event: SendCodeEvent) {
        when (event) {
            is SendCodeEvent.EmailQuery -> {
                state = state.copy(email = event.email)
//                    if (isValidEmail(state.email)) {
                sendVerificationCode()

            }
        }
    }

    private fun sendVerificationCode() {
        viewModelScope.launch {
            when (val result = sendCodeVerificationUseCase(state.email)) {
                is Result.Error -> {
                    state =
                        state.copy(isLoading = false, isValid = false, isAlreadyRegistered = false)
                }

                Result.Loading -> {
                    state = state.copy(isLoading = true)
                }

                is Result.Success -> {
                    when (result.data) {
                        400 -> {
                            state = state.copy(
                                isLoading = false,
                                isValid = true,
                                isAlreadyRegistered = true
                            )
                        }

                        200 -> {
                            state = state.copy(
                                isLoading = false,
                                isValid = true,
                                isAlreadyRegistered = false
                            )
                        }

                        0 -> {
                            state = state.copy(
                                isLoading = false,
                                isValid = false,
                                isAlreadyRegistered = false,
                                message = "Unknown Server Error"
                            )
                        }

                        else -> {
                            state = state.copy(
                                isLoading = false,
                                isValid = false,
                                isAlreadyRegistered = false
                            )
                        }
                    }
                }
            }
        }

    }
}