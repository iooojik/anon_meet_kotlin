package iooojik.anon.meet.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "messages")
data class MessageModel(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    @SerializedName("text")
    var text: String = "",
    @SerializedName("date")
    var date: String = "",
    @SerializedName("author")
    @Ignore
    val author: User,
    var isMine: Boolean = User.mUuid == author.uuid,
    @SerializedName("seen")
    var seen: Boolean = isMine
) {
    constructor() : this(null, "", "", User(), false, false)
}