package iooojik.anon.meet.models

import com.google.gson.annotations.SerializedName

data class TypingModel(
    @SerializedName("typing")
    val typing: Boolean = mTyping,
    @SerializedName("typingUser")
    val typingUser: User = mTypingUser
) {
    companion object {
        @JvmStatic
        var mTyping: Boolean = false

        @JvmStatic
        var mTypingUser: User = User()
    }
}