/*
 * Copyright 2021 Vitaliy Zarubin
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
 
package com.keygenqt.response

import com.keygenqt.response.HTTPResult.*
import com.keygenqt.response.extensions.toHTTPResult
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response


/**
 * Response Check with custom handling JSONObject
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
fun <T> Response<T>.responseCheckObject(
    bodyCheckSuccess: (Response<T>) -> Response<T> = { it },
    bodyCheckError: ((JSONObject) -> String?)? = null,
): Response<T> {
    return bodyCheckError?.let {
        this.responseCheck(bodyCheckSuccess) {
            when (it) {
                is JSONObject -> bodyCheckError.invoke(it)
                else -> null
            }
        }
    } ?: this.responseCheck()
}

/**
 * Response Check with custom handling JSONArray
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
fun <T> Response<T>.responseCheckArray(
    bodyCheckSuccess: (Response<T>) -> Response<T> = { it },
    bodyCheckError: ((JSONArray) -> String?)? = null,
): Response<T> {
    return bodyCheckError?.let {
        this.responseCheck(bodyCheckSuccess) {
            when (it) {
                is JSONArray -> bodyCheckError.invoke(it)
                else -> null
            }
        }
    } ?: this.responseCheck()
}

/**
 * Check status HTTP response retrofit2
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
fun <T> Response<T>.responseCheck(
    bodyCheckSuccess: (Response<T>) -> Response<T> = { it },
    bodyCheckError: ((Any) -> String?)? = null,
): Response<T> {
    return when (code().toHTTPResult()) {
        is Result200 -> bodyCheckSuccess.invoke(this)
        is Result400 -> throw Result400()
        is Result401 -> throw Result401()
        is Result404 -> throw Result404()
        is Result500 -> throw Result500()
        is Result403 -> throw this.parseApiError(bodyCheckError)?.let { Result403(it) } ?: Result403()
        is ResultUnknown -> throw this.parseApiError(bodyCheckError)?.let { ResultUnknown(code(), it) }
            ?: ResultUnknown(code())
    }
}

/**
 * Parse Api Error select body
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
private fun <T> Response<T>.parseApiError(bodyCheckError: ((Any) -> String?)? = null): String? {
    return this.errorBody()?.string()?.let {
        it.parseApiError(bodyCheckError)
    } ?: this.body().toString().parseApiError(bodyCheckError)
}

/**
 * Default parse Api Error from HTTP response retrofit2
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
private fun String.parseApiError(bodyCheckError: ((Any) -> String?)? = null): String? {
    return try {
        bodyCheckError?.let {
            if (startsWith("{")) {
                bodyCheckError.invoke(JSONObject(this))
            } else {
                bodyCheckError.invoke(JSONArray(this))
            }
        } ?: run {
            // fix for app
            val data = if (startsWith("{") && JSONObject(this).has("errors")) {
                JSONObject(this).getJSONArray("errors").toString()
            } else {
                this
            }
            // get json object
            val obj = if (data.startsWith("[")) {
                val arr = JSONArray(data)
                if (arr.length() == 0) throw Exception()
                arr.getJSONObject(0)
            } else {
                JSONObject(data)
            }
            // get message
            return if (obj.has("message")) {
                obj.getString("message")
            } else throw Exception()
        }
    } catch (ex: Exception) {
        null
    }
}