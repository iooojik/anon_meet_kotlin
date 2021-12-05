package iooojik.anon.meet.ui.chat

import androidx.lifecycle.ViewModel
import iooojik.anon.meet.models.MessageModel

class MessagesViewModel : ViewModel() {
    companion object {
        var messages = mutableListOf<MessageModel>()
    }
}