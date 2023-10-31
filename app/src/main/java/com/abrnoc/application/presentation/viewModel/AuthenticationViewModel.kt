package com.abrnoc.application.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrnoc.application.presentation.utiles.validateSignInRequest
import com.abrnoc.application.presentation.viewModel.event.SendCodeEvent
import com.abrnoc.application.presentation.viewModel.state.SendCodeState
import com.abrnoc.domain.auth.CheckMailCodeUseCase
import com.abrnoc.domain.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val checkMailUseCase: CheckMailCodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SendCodeState())
    val state: StateFlow<SendCodeState> = _state
    fun onEvent(event: SendCodeEvent) {
        when (event) {
            is SendCodeEvent.EmailQuery -> {
                _state.value = SendCodeState(isLoading = true)
                val isValid = validateSignInRequest(event.email)
                _state.value = SendCodeState(
                    isValid = isValid.isValid,
                    message = isValid.message,
                    isLoading = true
                )
                if (isValid.isValid) {
                    _state.value = SendCodeState(
                        email = event.email,
                        message = isValid.message,
                        isLoading = true
                    )
//                    if (isValidEmail(state.email)) {
                    sendVerificationCode()
                }

            }

            SendCodeEvent.ClearEvent -> {
                _state.value = SendCodeState(isLoading = false, isAlreadyRegistered = false)
            }
        }
    }

    private fun sendVerificationCode() {
        viewModelScope.launch {
            when (val result = checkMailUseCase(_state.value.email)) {
                is Result.Error -> {
                    _state.value =
                        SendCodeState(
                            isLoading = false,
                            isValid = false,
                            isAlreadyRegistered = false,
                            message = "send email task failed"
                        )
                }

                Result.Loading -> {
                    _state.value = SendCodeState(isLoading = true, message = "email was sent")
                }

                is Result.Success -> {
                    when (result.data) {
                        409 -> {
                            _state.value = SendCodeState(
                                isLoading = false,
                                isValid = true,
                                isAlreadyRegistered = true,
                                message = "server error, check your network"
                            )
                        }

                        400 -> {
                            _state.value = SendCodeState(
                                isLoading = false,
                                isValid = true,
                                isAlreadyRegistered = false,
                                message = "server error, check your network"
                            )
                        }

                        200 -> {
                            _state.value = SendCodeState(
                                isLoading = false,
                                isValid = true,
                                isAlreadyRegistered = false,
                                message = "email accepted"
                            )
                        }

                        0 -> {
                            _state.value = SendCodeState(
                                isLoading = false,
                                isValid = false,
                                isAlreadyRegistered = false,
                                message = "Unknown Server Error"
                            )
                        }

                        else -> {
                            _state.value = SendCodeState(
                                isLoading = false,
                                isValid = false,
                                isAlreadyRegistered = false,
                                message = "task failed"
                            )
                        }
                    }
                }
            }
        }

    }
}