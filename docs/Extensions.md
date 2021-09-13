### Extensions

Extensions for Retrofit2 etc

#### Response.responseCheck

Checking the response to an error, getting an error from the response and throw HTTPResult exception if necessary

```kotlin
suspend fun getListFavorites(page: Int): ResponseResult<List<FavoriteModel>> {
    return withContext(Dispatchers.IO) {
        executeWithResponse {
            api.getListFavorites(page)
                .responseCheck()
                .body()
                ?.toModels()
                ?: emptyList()
        }
    }
}
```

#### Response.responseCheck { bodyString -> }

Custom data processing if the standard does not work

```kotlin
suspend fun sendCode(emailOrPhone: String): ResponseResult<Boolean> {
    return withContext(Dispatchers.IO) {
        executeWithResponse {
            api.sendCode(contact = emailOrPhone)
                .responseCheck { _, body ->
                    JSONObject(body).apply {
                        if (!getBoolean("sentSuccess")) throw Result422(getString("errorMsg"))
                    }
                }
                .body()
                ?.getAsJsonPrimitive("sentSuccess")?.asBoolean ?: false
        }
    }
}
```

#### Int.toHTTPResult

Getting HTTPResult by error code

```kotlin
// throw result
fun getHTTPResult(code: Int): Nothing {
    throw code.toHTTPResult()
}

// get default value response message
fun getMessageResponse(code: Int): String? {
    return when(val result = code.toHTTPResult()) {
        is HTTPResult.Result200 -> result.message
        is HTTPResult.Result400 -> result.message
        is HTTPResult.Result401 -> result.message
        is HTTPResult.Result404 -> result.message
        is HTTPResult.Result500 -> result.message
        is HTTPResult.Result403 -> result.message
        is HTTPResult.ResultUnknown -> result.message
    }
}
```