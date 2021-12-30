package iooojik.anon.meet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import iooojik.anon.meet.data.dao.MessageDao
import iooojik.anon.meet.data.models.MessageModel


@Database(
    entities = [MessageModel::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract val messageDao:MessageDao
    companion object{
        @JvmStatic
        lateinit var instance: AppDatabase


        fun initDatabase(applicationContext: Context){
            synchronized(AppDatabase::class){
                instance = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "database"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}