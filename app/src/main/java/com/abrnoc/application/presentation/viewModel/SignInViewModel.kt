package com.abrnoc.application.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrnoc.application.presentation.mapper.mapToDomain
import com.abrnoc.application.presentation.utiles.isValidEmail
import com.abrnoc.application.presentation.viewModel.event.SignInEvent
import com.abrnoc.application.presentation.viewModel.state.SignInState
import com.abrnoc.application.presentation.viewModel.state.model.SignInObj
import com.abrnoc.domain.auth.SignInPasswordUseCase
import com.abrnoc.domain.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInPasswordUseCase: SignInPasswordUseCase
) : ViewModel() {
    var state by mutableStateOf(SignInState())
    private var searchJob: Job? = null
    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.SignInQuery -> {
                state = state.copy(signIn = SignInObj(event.email, event.password))
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    if (isValidEmail(state.signIn?.email)) {
                        requestSignIn()
                    }
                }

            }
        }
    }

    private suspend fun requestSignIn() {
        viewModelScope.launch {
            signInPasswordUseCase(state.signIn!!.mapToDomain()).collect { result ->
                when (result) {
                    is Result.Error -> {
                        state = state.copy(isLoading = false, error = result.exception.toString(), isSuccessful = false)
                    }

                    Result.Loading -> {
                        state = state.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        println(" the result is *** ${result.data}")
                        state = state.copy(isLoading = false)
                    }
                }
            }

        }

    }

}

