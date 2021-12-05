package iooojik.anon.meet.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("user")
    val user: User,
    @SerializedName("tokenData")
    val tokenData: TokenData
)