package iooojik.anon.meet.data.models

import com.google.gson.annotations.SerializedName
import iooojik.anon.meet.data.models.user.TokenData
import iooojik.anon.meet.data.models.user.User

data class LoginResponse(

    @SerializedName("user")
    val user: User,
    @SerializedName("tokenData")
    val tokenData: TokenData
)