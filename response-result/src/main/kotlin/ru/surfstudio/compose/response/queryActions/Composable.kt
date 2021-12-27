/**
 * Copyright © 2021 Surf. All rights reserved.
 */
package ru.surfstudio.compose.response.queryActions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun QueryActionsState(
    state: QueryState,
    block: QueryState.() -> Unit
) {
    var statusSaved: String? by rememberSaveable { mutableStateOf(null) }
    if (statusSaved != state.toString()) {
        statusSaved = state.toString()
        block.invoke(state)
    }
}
