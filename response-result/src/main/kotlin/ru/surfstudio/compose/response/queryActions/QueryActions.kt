/**
 * Copyright © 2021 Surf. All rights reserved.
 */
package ru.surfstudio.compose.response.queryActions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.surfstudio.compose.response.ResponseResult
import ru.surfstudio.compose.response.extensions.*

class QueryActions(private val viewModel: ViewModel) {

    /**
     * Action state
     */
    private val _state: MutableStateFlow<QueryState> = MutableStateFlow(QueryState.Start)

    /**
     * [StateFlow] for [_state]
     */
    val state: StateFlow<QueryState> get() = _state.asStateFlow()

    /**
     * Launch query
     */
    fun <T> queryLaunch(
        query: suspend CoroutineScope.() -> ResponseResult<T>
    ) {
        // set loading
        _state.value = QueryState.Action
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
        _state.value = QueryState.Success(data)
    }

    /**
     * Set state exception Error
     */
    private fun setError(exception: Exception) {
        _state.value = QueryState.Error(exception)
    }
}
