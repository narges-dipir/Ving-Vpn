package com.abrnoc.application.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrnoc.application.presentation.viewModel.event.SendVerificationEvent
import com.abrnoc.application.presentation.viewModel.state.VerificationCodeState
import com.abrnoc.domain.auth.SignUpVerificationCodeUseCase
import com.abrnoc.domain.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationCodeViewModel @Inject constructor(
    private val signUpWithVerificationCodeUseCase: SignUpVerificationCodeUseCase
) : ViewModel() {
    var state by mutableStateOf(VerificationCodeState())
    fun onEvent(event: SendVerificationEvent) {
        when (event) {
            is SendVerificationEvent.SignInQuery -> {
                sendCodeForVerification()
            }
        }

    }

    private fun sendCodeForVerification() {
        viewModelScope.launch {
            signUpWithVerificationCodeUseCase(state.verificationObject!!).collect { result ->
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


}