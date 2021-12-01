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
 
package ru.surfstudio.compose.response

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LocalTryExecuteWithResponse {

    private val _tryExecuteWithResponse: MutableSharedFlow<Exception> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * Hot flow for listening exceptions
     *
     * @since 0.0.4
     * @author Vitaliy Zarubin
     */
    val current: SharedFlow<Exception> get() = _tryExecuteWithResponse.asSharedFlow()

    /**
     * Emit error exception
     *
     * @since 0.0.4
     * @author Vitaliy Zarubin
     */
    fun tryEmit(exception: Exception) {
        _tryExecuteWithResponse.tryEmit(exception)
    }

    /**
     * Exception handling
     *
     * @since 0.0.5
     * @author Vitaliy Zarubin
     */
    inline fun <T> executeWithResponse(emit: Boolean = true, body: () -> T): ResponseResult<T> {
        return try {
            ResponseResult.Success(body.invoke())
        } catch (e: Exception) {
            if (emit) {
                tryEmit(e)
            }
            ResponseResult.Error(e)
        }
    }
}