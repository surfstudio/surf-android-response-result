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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.surfstudio.compose.response.ResponseResult
import ru.surfstudio.compose.response.extensions.*

class ResponseStates(
    private val viewModel: ViewModel,
    private val errorHandler: (Exception) -> ResponseState = { ResponseState.Error(it) }
) {
    /**
     * Action state
     */
    private val _state: MutableStateFlow<ResponseState> = MutableStateFlow(ResponseState.Start)

    /**
     * [StateFlow] for [_state]
     */
    val state: StateFlow<ResponseState> get() = _state.asStateFlow()

    /**
     * Launch query
     */
    fun <T> queryLaunch(
        query: suspend CoroutineScope.() -> ResponseResult<T>
    ) {
        // set loading
        _state.value = ResponseState.Action
        // launch scope
        viewModel.viewModelScope.launch {
            query()
                .success(::setSuccess)
                .error(::setError)
                .errorUnknownHost(::setError)
                .errorTimeout(::setError)
                .empty { setSuccess(null) }
        }
    }

    /**
     * Set state Success
     */
    private fun <T> setSuccess(data: T?) {
        _state.value = ResponseState.Success(data)
    }

    /**
     * Set state exception Error
     */
    private fun setError(exception: Exception) {
        _state.value = errorHandler.invoke(exception)
    }
}
