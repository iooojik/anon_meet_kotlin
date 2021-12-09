package iooojik.anon.meet.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import iooojik.anon.meet.AppDatabase
import iooojik.anon.meet.log

class MessagesViewModel : ViewModel() {
    companion object {
        val messages : LiveData<MutableList<MessageModel>>
            get(){
                return AppDatabase.instance.messageDao.getAll()
            }
    }
}