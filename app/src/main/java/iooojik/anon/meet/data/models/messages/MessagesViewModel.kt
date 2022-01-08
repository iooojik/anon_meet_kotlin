package iooojik.anon.meet.data.models.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import iooojik.anon.meet.AppDatabase

class MessagesViewModel : ViewModel() {
    companion object {
        val messages : LiveData<MutableList<MessageModel>>
            get(){
                return AppDatabase.instance.messageDao.getAllToObserve()
            }
    }
}