package by.amakarevich.medlike.data

data class UserMedCenterLike (
    val userID: String? = null,
    val medcenter: List<NameMedCenters>? = null
)
data class NameMedCenters (
    val nameMedcenter: String? = null,
    val like: Int? = null
)