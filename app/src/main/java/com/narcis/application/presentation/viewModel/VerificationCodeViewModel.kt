package com.narcis.application.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narcis.application.presentation.viewModel.event.SendVerificationEvent
import com.narcis.application.presentation.viewModel.state.SendCodeState
import com.narcis.application.presentation.viewModel.state.VerificationCodeState
import com.narcis.domain.auth.SendVerificationCodeUseCase
import com.narcis.domain.auth.SignUpVerificationCodeUseCase
import com.narcis.domain.common.Result
import com.narcis.domain.model.VerificationObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationCodeViewModel @Inject constructor(
    private val signUpWithVerificationCodeUseCase: SignUpVerificationCodeUseCase,
    private val sendCodeVerificationUseCase: SendVerificationCodeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(VerificationCodeState())
    private val _resendState = MutableStateFlow(SendCodeState())
    val resendState: StateFlow<SendCodeState> = _resendState
    var email = ""
    private var password = ""
    init {
        savedStateHandle.get<String>("email")?.let {eml ->
           email = eml
        }

        savedStateHandle.get<String>("password")?.let {psw ->
           password = psw
        }
        resendVerificationCode(email)
    }
    fun onEvent(event: SendVerificationEvent) {
        when (event) {
            is SendVerificationEvent.SignInQuery -> {
                sendCodeForVerification(VerificationObject(password = password, email = email, code = event.code))
            }
            is SendVerificationEvent.EmailQuery -> {
                resendVerificationCode(email)
            }
        }

    }

    private fun sendCodeForVerification(verificationObject: VerificationObject) {
        viewModelScope.launch {
            signUpWithVerificationCodeUseCase(verificationObject).collect { result ->
                when (result) {
                    is Result.Error -> {
                        state = state.copy(
                            isLoading = false,
                            isSuccessful = false,
                            error = result.exception.toString()
                        )
                    }

                    Result.Loading -> {
                        state = state.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        state = state.copy(isLoading = false, isSuccessful = true)
                    }
                }

            }

        }
    }

    private fun resendVerificationCode(email: String) {
        viewModelScope.launch {
            when (val result = sendCodeVerificationUseCase(email)) {
                is Result.Error -> {
                    _resendState.value =
                        SendCodeState(isLoading = false, isValid = false, isAlreadyRegistered = false, message = "send email task failed")
                }

                Result.Loading -> {
                    _resendState.value = SendCodeState(isLoading = true, message = "email was sent")
                }

                is Result.Success -> {
                    when (result.data) {
                        400 -> {
                            _resendState.value = SendCodeState(
                                isLoading = false,
                                isValid = true,
                                isAlreadyRegistered = true,
                                message = "server error, check your network"
                            )
                        }

                        200 -> {
                            _resendState.value = SendCodeState(
                                isLoading = false,
                                isValid = true,
                                isAlreadyRegistered = false,
                                message = "email accepted"
                            )
                        }

                        0 -> {
                            _resendState.value = SendCodeState(
                                isLoading = false,
                                isValid = false,
                                isAlreadyRegistered = false,
                                message = "Unknown Server Error"
                            )
                        }

                        else -> {
                            _resendState.value = SendCodeState(
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