Handling request states in compose

### States

* Start - Start state
* Action - Query in action
* Error - Response error
* Success - Response success with data response

### Usage in ViewModel

```kotlin
/**
 * State query
 */
val query1 = ResponseStates(this)

/**
 * Query fun in viewModel
 */
private fun signInCode(code: String) {
    query1.queryLaunch {
        apiService.oauthCode(code = code).success { AuthUser.login(it) }
    }
}
```

You can added custom error handler

```kotlin
/**
 * Custom error handler
 */
fun errorHandler(exception: Exception): ResponseState {
    return when (exception) {
        is ResponseException -> ResponseState.Error(exception)
        else -> ResponseState.Error(ResponseException.ExceptionUnknown())
    }
}

/**
 * State actions
 */
val query1 = ResponseStates(this, ::errorHandler)
```

### Usage in Jetpack Compose

```kotlin
val state1 by viewModel.query1.state.collectAsState()

ResponseComposable(state1) {
    when (this) {
        is ResponseState.Start -> {
            // start page
        }
        is ResponseState.Action -> {
            // start loader
        }
        is ResponseState.Success<*> -> {
            when(data) {
                is UserModel -> {
                    // success query user model
                }
                else -> {
                    // success other
                }
            }
        }
        is ResponseState.Error -> {
            when(exception) {
                is UnknownHostException -> {
                    // error network
                }
                else -> {
                    // errors other
                }
            }
        }
    }
}
```