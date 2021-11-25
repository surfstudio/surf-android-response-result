package com.keygenqt.response

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