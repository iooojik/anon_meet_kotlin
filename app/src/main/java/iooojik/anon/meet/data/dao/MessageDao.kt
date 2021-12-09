package iooojik.anon.meet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import iooojik.anon.meet.data.models.MessageModel


@Dao
interface MessageDao {
    @Query("SELECT * FROM messages")
    fun getAll(): LiveData<MutableList<MessageModel>>

    @Insert
    fun insert(message: MessageModel)

    @Delete
    fun delete(message: MessageModel)

    @Query("DELETE FROM messages")
    fun clearTable()
}