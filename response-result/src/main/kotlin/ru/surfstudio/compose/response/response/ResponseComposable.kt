/*
 * Copyright 2021 Surf LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.surfstudio.compose.response.response

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Listen in compose state query
 *
 * @author Vitaliy Zarubin
 */
@Composable
fun ResponseComposable(
    state: ResponseState,
    clear: () -> Unit,
    block: ResponseState.() -> Unit
) {
    var statusSaved: String? by rememberSaveable { mutableStateOf(null) }

    if (statusSaved != state.toString()) {
        statusSaved = state.toString()
        clear.invoke()
        block.invoke(state)
    }
}