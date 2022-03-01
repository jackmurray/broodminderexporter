package com.c0rporation.broodminderexporter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainViewModel (private val savedState: SavedStateHandle) : ViewModel() {
    var serverAddress by mutableStateOf("")
}
