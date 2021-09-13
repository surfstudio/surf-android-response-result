package com.keygenqt.response

/**
 * Base Response Processing Class
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
sealed class ResponseResult<out R> {
    data class Success<out T>(val data: T) : ResponseResult<T>()
    data class Error(val exception: Exception) : ResponseResult<Nothing>()
}