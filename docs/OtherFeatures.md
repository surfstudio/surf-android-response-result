### Other features

Additional features and features

#### executeWithResponse

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

#### ResponseModel, ResponseModelRelation

Model interfaces to unify fields and make it easier to get the last page for some frameworks

```kotlin
@Entity
@Immutable
data class FeedModel(
    @PrimaryKey override val id: String,
    val brandName: String,
    @Ignore private val banners: List<FeedBannerModel>
) : ResponseModel

@Entity
@Immutable
data class FeedBannerModel(
    @PrimaryKey override val id: String,
    override val ownerId: String,
    @Embedded val image: FeedBannerImageModel,
    @Embedded val expandData: FeedBannerDataModel,
    val title: String
) : ResponseModelRelation

data class FeedRelation(
    @Embedded
    val owner: FeedModel,
    @Relation(parentColumn = "id", entityColumn = "ownerId")
    val banners: List<FeedBannerModel>
)
```