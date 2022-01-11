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
package ru.surfstudio.compose.response.request

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.surfstudio.compose.response.ResponseResult

/**
 * Feature for processing requests. Global single subscription for error handling.
 */
object RequestHandler {

    /**
     * [Job] collect for cancel
     */
    private var job: Job? = null

    /**
     * State error handler
     */
    private val STATE: MutableSharedFlow<Exception> = MutableSharedFlow()

    /**
     * Single collect error handler
     */
    suspend fun singleCollect(action: suspend (value: Exception) -> Unit) {
        job?.cancel()
        coroutineScope {
            job = launch {
                STATE.asSharedFlow().collect {
                    action.invoke(it)
                }
            }
        }
    }

    /**
     * Emit error [Exception]
     */
    suspend fun emit(exception: Exception) {
        STATE.emit(exception)
    }

    /**
     * Try emit error [Exception]
     */
    fun tryEmit(exception: Exception) {
        STATE.tryEmit(exception)
    }

    /**
     *  Request handler execute
     */
    suspend inline fun <T> executeRequest(
        emit: Boolean = true,
        crossinline body: suspend () -> T
    ): ResponseResult<T> {
        return try {
            ResponseResult.Success(body.invoke())
        } catch (e: Exception) {
            if (emit) {
                emit(e)
            }
            ResponseResult.Error(e)
        }
    }
}