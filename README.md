Android Response Result
===================

![picture](data/just-image.png)

#### Connection:

```gradle
repositories {
    maven("https://artifactory.keygenqt.com/artifactory/open-source")
}
dependencies {
    implementation("com.keygenqt.response:android-response-result:0.0.1")
}
```

### Features

* ResponseResult - Base class response
* HTTPResult - Sealed class with results exception with code and message
* ResponseModel, ResponseModelRelation - Interfaces to help automate working with models

### Extensions ResponseResult

* ResponseResult.size - The number of items in the response
* ResponseResult.isEmpty - Checking if the response is empty
* ResponseResult.isSucceeded - Checking that the request was successful
* ResponseResult.isError - Checking that the request was with an error
* ResponseResult.pagingSucceeded - Extension convenient for working with PagingSource
* ResponseResult.success - Response Result success
* ResponseResult.error - Response Result error
* ResponseResult.done - End of request for any outcome
* ResponseResult.errorUnknownHost - No internet error
* ResponseResult.isEndYii2 - In some frameworks, the absence of a page always returns the latest data

### Extensions Retrofit2
* responseCheck - Check status HTTP response retrofit2

### Extensions Other

* Int.toHTTPResult - Code int to HTTPResult
* executeWithResponse - Exception handling

### Usage

```kotlin

// Example service query
suspend fun getListFavorites(page: Int): ResponseResult<List<FavoriteModel>> {
    return withContext(Dispatchers.IO) {
        executeWithResponse { // Exception processing
            api.getListFavorites(page)
                .responseCheck() // Check status HTTP response codes retrofit2
                .body()
                ?.toModels() // custom mapper
                ?: emptyList()
        }
    }
}

// Example custom check body response
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

// Example response processing
val response = apiService.getListFavorites(
    page = page ?: 0
)

response.success { models ->
    // Response Result success
}.error {
    // Response Result error
}.done {
    // End of request for any outcome
}.errorUnknownHost {
    // No internet error
}

// The number of items in the response
println(response.size)

// Checking if the response is empty
println(response.isEmpty)

// Checking that the request was successful
println(response.isSucceeded)

// Checking that the request was with an error
println(response.isError)

// In some frameworks, the absence of a page always returns the latest data
response.isEndYii2(state.lastItemOrNull()?.id)

// MediatorResult
MediatorResult.Success(
    endOfPaginationReached = response.isError
            || response.isEmpty
            || response.isEndYii2(state.lastItemOrNull()?.id)
)

// PagingSource pagingSucceeded
return repository.getListChats(search = search, offset = offset).pagingSucceeded { data ->
    LoadResult.Page(
        data = data,
        prevKey = if (offset == 0) null else offset,
        nextKey = if (data.isEmpty()) null else offset + ConstantsPaging.PAGE_LIMIT
    )
}
```

# License

```
Copyright 2021 Vitaliy Zarubin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
