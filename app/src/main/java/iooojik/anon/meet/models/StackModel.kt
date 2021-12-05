package iooojik.anon.meet.models

import com.google.gson.annotations.SerializedName


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