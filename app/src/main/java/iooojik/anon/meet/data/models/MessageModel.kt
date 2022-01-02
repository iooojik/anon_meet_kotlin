package iooojik.anon.meet.data.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "messages")
class MessageModel(
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
    var seen: Boolean = false
) {
    constructor() : this(null, "", "", User(), false, false)
}

class MessageViewModel : ViewModel() {
    var messageMutableLiveData = MutableLiveData<MessageModel>()

    fun updateModel(messageModel: MessageModel): MessageViewModel {
        messageMutableLiveData.value = messageModel
        return this
    }
}