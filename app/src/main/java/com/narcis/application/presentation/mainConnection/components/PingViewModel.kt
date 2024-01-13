package com.narcis.application.presentation.mainConnection.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PingViewModel @Inject constructor(): ViewModel() {
    private var _ping = MutableStateFlow(mutableListOf(""))
    val ping: StateFlow<MutableList<String>> = _ping
    private var firstTime = false

     fun startPing(totalSize: Int) {
         val tempList = mutableListOf<String>()
         if (!firstTime) {
             repeat(totalSize + 1) {
                 tempList.add("ping_four_bars")
             }
             _ping.value = tempList
             firstTime = true
         }
         viewModelScope.launch(Dispatchers.IO) {
             getNewBars(totalSize = totalSize)

         }
     }

    private suspend fun getNewBars(totalSize : Int) {
        delay(10_000)
        val barsSample = listOf(
            "three",
            "three",
            "three",
            "four",
            "four",
            "four",
            "one",
            "two",
            "three",
            "three",
            "three",
            "four",
            "four",
            "four"
        )
        val newPngs = mutableListOf<String>("")
        for (i in 0 until totalSize+1) {
            newPngs.add("ping_${barsSample.random()}_bars")
        }
       _ping.value = newPngs
    }
}