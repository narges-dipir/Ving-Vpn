package com.abrnoc.application.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrnoc.application.presentation.viewModel.event.SendVerificationEvent
import com.abrnoc.application.presentation.viewModel.state.VerificationCodeState
import com.abrnoc.domain.auth.SignUpVerificationCodeUseCase
import com.abrnoc.domain.common.Result
import com.abrnoc.domain.model.VerificationObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationCodeViewModel @Inject constructor(
    private val signUpWithVerificationCodeUseCase: SignUpVerificationCodeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(VerificationCodeState())
    private var email = ""
    private var password = ""
    init {
        savedStateHandle.get<String>("email")?.let {eml ->
           email = eml
        }

        savedStateHandle.get<String>("password")?.let {psw ->
           password = psw
        }
    }
    fun onEvent(event: SendVerificationEvent) {
        when (event) {
            is SendVerificationEvent.SignInQuery -> {
                sendCodeForVerification(VerificationObject(password = password, email = email, code = event.code))
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


}