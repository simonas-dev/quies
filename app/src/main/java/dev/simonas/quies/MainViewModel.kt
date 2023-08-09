package dev.simonas.quies

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor() : ViewModel() {

    val state: StateFlow<State> = MutableStateFlow(
        State(
            title = "Hello!",
        )
    )

    data class State(
        val title: String = "",
    )
}
