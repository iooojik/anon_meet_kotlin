package iooojik.anon.meet.models

import com.google.gson.annotations.SerializedName

data class MessageModel (
    @SerializedName("text")
        val text : String = "",
    @SerializedName("date")
        var date : String = "",
    @SerializedName("author")
        val author: User,
    var isMine: Boolean = User.mUuid == author.uuid
)