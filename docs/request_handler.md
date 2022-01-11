Feature for processing requests. Global single subscription for error handling.

### Usage singleCollect

```kotlin
// Listen global errors responses
init {
    viewModelScope.launch {
        RequestHandler.singleCollect {
            val message = it.message ?: context.getString(R.string.error_something_wrong)
            // show toast
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            // logcat
            Timber.e(it)
        }
    }
}
```

### Usage executeRequest

```kotlin
/**
 * Mapper response single model
 */
fun UserResponse.toModel(): UserModel {
    return UserModel(
        id = id.toString(),
        login = login ?: "",
    )
}

/**
 * Mapper response list models
 */
fun List<UserResponse>.toModels(): List<UserModel> {
    return map { it.toModel() }
}

/**
 * Interface retrofit api
 */
@GET("/user")
suspend fun getUser(): Response<UserResponse>

/**
 * Service request
 */
suspend fun getUserModel(): ResponseResult<UserModel> {
    return withContext(Dispatchers.IO) {
        executeRequest(emit = true /* emit global error or not */) {
            api.getUser().body()!!.toModel()
        }
    }
}
```
