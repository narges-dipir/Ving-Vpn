package com.abrnoc.application.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.abrnoc.application.presentation.viewModel.state.DefaultConfigState
import com.abrnoc.application.repository.IDefaultConfigRepository
import com.abrnoc.application.repository.model.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultConfigViewModel @Inject constructor(
    private val defaultConfigRepository: IDefaultConfigRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<DefaultConfigState?>(null)
    private val state: StateFlow<DefaultConfigState?> = _state
    val defaultConfigFlow: LiveData<DefaultConfigState?> = state.asLiveData()

    init {
        getAllConfigs()
    }
    private fun getAllConfigs() {
        viewModelScope.launch {
            defaultConfigRepository.getAppConfigs().collect { result ->
                when (result) {
                    is ResultWrapper.Error -> {
                        _state.value = DefaultConfigState(
                            error = result.exception.message ?: "An unexpected error occured",
                        )
                    }

                    ResultWrapper.Loading -> {
                        _state.value = DefaultConfigState(isLoading = true)
                    }

                    is ResultWrapper.Success -> {
                        _state.value = DefaultConfigState(configs = result.data)
                    }
                }
            }
        }
    }
}
