package iooojik.anon.meet.data.models

import com.google.gson.annotations.SerializedName

data class TokenData(

    @SerializedName("token")
    val token: String,
    @SerializedName("tokenHeader")
    val tokenHeader: String
)