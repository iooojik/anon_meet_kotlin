package iooojik.anon.meet.data.models.search

import com.google.gson.annotations.SerializedName
import iooojik.anon.meet.data.models.user.User


data class StackModel(
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("available")
    val available: Boolean,
    @SerializedName("isEnd")
    val isEnd: Boolean,
    @SerializedName("participants")
    val participants: List<User>,
    @SerializedName("messages")
    val messages: List<String>
)