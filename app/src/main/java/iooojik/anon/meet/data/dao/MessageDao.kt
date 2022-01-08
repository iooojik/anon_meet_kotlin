package iooojik.anon.meet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import iooojik.anon.meet.data.models.messages.MessageModel


@Dao
interface MessageDao {
    @Query("SELECT * FROM messages")
    fun getAllToObserve(): LiveData<MutableList<MessageModel>>

    @Query("SELECT * FROM messages")
    fun getAll(): List<MessageModel>

    @Insert
    fun insert(message: MessageModel)

    @Delete
    fun delete(message: MessageModel)

    @Update
    fun update(message: MessageModel)

    @Query("DELETE FROM messages")
    fun clearTable()
}