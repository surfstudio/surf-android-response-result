/**
 * Copyright © 2021 Surf. All rights reserved.
 */
package ru.surfstudio.compose.response.queryActions

sealed class QueryState {

    /**
     * Start state
     */
    object Start : QueryState()

    /**
     * Action state
     */
    object Action : QueryState()

    /**
     * Error state with value error
     */
    data class Error(val exception: Exception) : QueryState()

    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : QueryState()
}
