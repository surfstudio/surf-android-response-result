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

import ru.surfstudio.compose.response.HTTPResult
import ru.surfstudio.compose.response.HTTPResult.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response

/**
 * Check status HTTP response retrofit2
 *
 * @since 0.0.2
 * @author Vitaliy Zarubin
 */
fun <T> Response<T>.responseCheck(
    bodyCheck: ((code: HTTPResult, body: String) -> Unit)? = null,
): Response<T> {
    val codeResult = code().toHTTPResult()
    val body = this.getStringBody()
    return bodyCheck?.invoke(codeResult, body).let { null } ?: run {
        when (codeResult) {
            is Result200 -> this
            is Result400 -> throw Result400()
            is Result401 -> throw Result401()
            is Result404 -> throw Result404()
            is Result500 -> throw Result500()
            is Result403 -> throw body.parseApiError()?.let { Result403(it) } ?: Result403()
            is ResultUnknown -> throw body.parseApiError()?.let { ResultUnknown(code(), it) } ?: ResultUnknown(code())
        }
    }
}

/**
 * Get String from response
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
private fun <T> Response<T>.getStringBody(): String {
    return this.errorBody()?.string() ?: this.body().toString()
}

/**
 * Default parse Api Error from HTTP response retrofit2
 *
 * @since 0.0.1
 * @author Vitaliy Zarubin
 */
private fun String.parseApiError(): String? {
    return try {
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
    } catch (ex: Exception) {
        null
    }
}