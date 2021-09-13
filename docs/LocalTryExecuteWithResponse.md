### LocalTryExecuteWithResponse

An object to catch errors and allow you to subscribe and listen to them

#### LocalTryExecuteWithResponse.executeWithResponse

Catching an Exception and generating a ResponseResult

```kotlin
suspend fun getListFavorites(page: Int): ResponseResult<List<FavoriteModel>> {
    return withContext(Dispatchers.IO) {
        executeWithResponse {
            throw Result422()
        }
    }
}
```

#### LocalTryExecuteWithResponse.current

It is possible to subscribe to SharedFlow and listen to global errors

```kotlin
val error by LocalTryExecuteWithResponse.current.collectAsState(null)
LaunchedEffect(error) {
    error?.let {
        Timber.e("------------------- Global error listener")
        Timber.e(it)
    }
}
```

