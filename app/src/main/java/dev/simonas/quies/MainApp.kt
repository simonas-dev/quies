package dev.simonas.quies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun AppMain(
    viewModel: MainViewModel,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    AppMain(
        state = state.value
    )
}

@Composable
internal fun AppMain(
    state: MainViewModel.State,
) {
    MaterialTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = state.title
                )
            }
        }
    }
}

@Preview
@Composable
private fun AppMainPreview() {
    AppMain(
        state = MainViewModel.State(
            title = "Hello!"
        )
    )
}
