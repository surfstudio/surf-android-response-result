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
package ru.surfstudio.compose.response.extensions

import androidx.paging.PagingSource
import ru.surfstudio.compose.response.ResponseResult
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * The number of items in the response
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
val ResponseResult<*>?.size
    get() = if (this != null
        && this is ResponseResult.Success
        && data != null
        && data is List<*>
    ) {
        data.size
    } else if (this != null
        && this is ResponseResult.Success
        && data != null
    ) {
        1
    } else {
        0
    }

/**
 * Checking if the response is empty
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
val ResponseResult<*>?.isEmpty get() = this.size == 0

/**
 * Checking that the request was successful
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
val ResponseResult<*>?.isSucceeded get() = this != null && this is ResponseResult.Success && data != null

/**
 * Checking that the request was with an error
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
val ResponseResult<*>?.isError get() = this != null && this is ResponseResult.Error

/**
 * Extension convenient for working with PagingSource
 *
 * <pre>
 * {@code
 *   return apiService.getListUsers(search = search, offset = offset).pagingSucceeded { data ->
 *       LoadResult.Page(
 *           data = data,
 *           prevKey = if (offset == 0) null else offset,
 *           nextKey = if (data.isEmpty()) null else offset + ConstantsPaging.PAGE_LIMIT
 *       )
 *   }
 * }
 * </pre>
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
inline infix fun <T, Value : Any> ResponseResult<T>.pagingSucceeded(
    predicate: (data: T) -> PagingSource.LoadResult<Int, Value>,
): PagingSource.LoadResult<Int, Value> {
    return if (this is ResponseResult.Success && this.data != null) {
        predicate.invoke(this.data)
    } else {
        if (this is ResponseResult.Error) {
            PagingSource.LoadResult.Error(this.exception)
        } else {
            PagingSource.LoadResult.Error(RuntimeException("Error response"))
        }
    }
}

/**
 * Response Result success
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
inline infix fun <T> ResponseResult<T>.success(predicate: (data: T) -> Unit): ResponseResult<T> {
    if (this is ResponseResult.Success && this.data != null) {
        predicate.invoke(this.data)
    }
    return this
}

/**
 * Response Result success null data
 *
 * @since 0.0.10
 * @author Vitaliy Zarubin
 */
inline infix fun <T> ResponseResult<T>.empty(predicate: () -> Unit): ResponseResult<T> {
    if (this is ResponseResult.Success && this.data == null) {
        predicate.invoke()
    }
    return this
}

/**
 * Response Result error
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
inline infix fun <T> ResponseResult<T>.error(predicate: (data: Exception) -> Unit): ResponseResult<T> {
    if (this is ResponseResult.Error) {
        if (this.exception !is UnknownHostException && this.exception !is SocketTimeoutException) {
            predicate.invoke(this.exception)
        }
    }
    return this
}

/**
 * No internet error
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
inline infix fun <T> ResponseResult<T>.errorUnknownHost(predicate: (data: Exception) -> Unit): ResponseResult<T> {
    if (this is ResponseResult.Error) {
        if (this.exception is UnknownHostException) {
            predicate.invoke(this.exception)
        }
    }
    return this
}

/**
 * No internet error
 *
 * @since 0.0.10
 * @author Vitaliy Zarubin
 */
inline infix fun <T> ResponseResult<T>.errorTimeout(predicate: (data: Exception) -> Unit): ResponseResult<T> {
    if (this is ResponseResult.Error) {
        if (this.exception is SocketTimeoutException) {
            predicate.invoke(this.exception)
        }
    }
    return this
}

/**
 * End of request for any outcome
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
inline infix fun <T> ResponseResult<T>.done(predicate: () -> Unit): ResponseResult<T> {
    predicate.invoke()
    return this
}