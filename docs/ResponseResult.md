### ResponseResult

Base class for intercepting events

#### Extensions ResponseResult

* ResponseResult.size - The number of items in the response
* ResponseResult.isEmpty - Checking if the response is empty
* ResponseResult.isSucceeded - Checking that the request was successful
* ResponseResult.isError - Checking that the request was with an error
* ResponseResult.pagingSucceeded - Extension convenient for working with PagingSource
* ResponseResult.success - Response Result success
* ResponseResult.error - Response Result error
* ResponseResult.done - End of request for any outcome
* ResponseResult.errorUnknownHost - No internet error

#### Example base service query

```kotlin
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
```

#### Example of processing a response

```kotlin
apiService.getListFavorites(page = 0)
    .success { models ->
        // Response Result success
    }.error { ex: Exception ->
        // Response Result error
    }.errorUnknownHost { ex: Exception ->
        // No internet error
    }.done {
        // End of request for any outcome
    }
```

#### Example of processing a response for PagingSource

```kotlin
return repository.getListFavorites(offset = offset).pagingSucceeded { data ->
    LoadResult.Page(
        data = data,
        prevKey = if (offset == 0) null else offset,
        nextKey = if (data.isEmpty()) null else offset + ConstantsPaging.PAGE_LIMIT
    )
}
```

#### Other extensions

```kotlin
// Example response processing
val response = apiService.getListFavorites(
    page = page ?: 0
)

// The number of items in the response
println(response.size)

// Checking if the response is empty
println(response.isEmpty)

// Checking that the request was successful
println(response.isSucceeded)

// Checking that the request was with an error
println(response.isError)
```