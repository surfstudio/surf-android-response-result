Response processing class.

### Checks response

```kotlin
val response = apiService.getUserFollowers(
    page = 1,
)

// get size
response.size

// check is empty response
response.isEmpty

// check is error response
response.isError

// check is success response
response.isSucceeded
```

### Callbacks response

```kotlin
val response = apiService.getUserFollowers(
    page = 1,
)

// success response
response.success {
    // it - data response
}

// empty success response
response.empty {

}

// error network
response.errorUnknownHost {
    // it - exception
}

// error timeout
response.errorTimeout {
    // it - exception
}

// errors without error network & timeout
response.error {
    // it - exception
}

// callback success or error anyway
response.done {
    // query stop
}
```