package iooojik.anon.meet.net.sockets

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import iooojik.anon.meet.AppDatabase
import iooojik.anon.meet.R
import iooojik.anon.meet.activity.MainActivity
import iooojik.anon.meet.data.models.MessageModel
import iooojik.anon.meet.data.models.SeenModel
import iooojik.anon.meet.data.models.TypingModel
import iooojik.anon.meet.data.models.User
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import iooojik.anon.meet.ui.chat.ChatProcessFragment
import ua.naiksoftware.stomp.dto.StompMessage
import java.text.SimpleDateFormat
import java.util.*


class ChatService : Service() {

    private lateinit var preferencesManager: SharedPreferencesManager
    private var roomUuid = ""
    private var data: PendingIntent? = null

    companion object {
        const val NOTIFICATION_ID = 101
        const val NOTIFICATION_ID2 = 102
        const val CHANNEL_ID = "channelID"
        const val CHANNEL_ID2 = "channelID2"
    }

    override fun onCreate() {
        SocketConnections.connectToServer(this)
        runAsForeground()
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        data = intent!!.getParcelableExtra("pendingIntent")

        checkAndConnectToChat()
        return START_REDELIVER_INTENT
    }

    private fun checkAndConnectToChat() {
        SocketConnections.connectToServer(this)
        preferencesManager = SharedPreferencesManager(this)
        preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
        val rUuid = preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "")
        if (rUuid != null && rUuid.toString().trim().isNotBlank()) roomUuid = rUuid.toString()
        SocketConnections.connectToTopic("/topic/$roomUuid/message.topic", ::onMessageReceived)
    }

    private fun onMessageReceived(topicMessage: StompMessage) {
        if (topicMessage.payload.trim().isNotBlank()) {
            when {
                topicMessage.payload.contains("typing") -> {
                    //пользователь набирает сообщение
                    val typingModel = Gson().fromJson(topicMessage.payload, TypingModel::class.java)
                    if (typingModel.typingUser.uuid != User.mUuid) {
                        val intent = Intent(ChatProcessFragment.adapterIntentFilterName)
                        intent.putExtra("typing", typingModel.typing)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    }
                }
                topicMessage.payload.contains("endChat") -> {
                    //завершается чат
                    preferencesManager = SharedPreferencesManager(this)
                    preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
                    preferencesManager.clearAll()
                    val intent = Intent(ChatProcessFragment.adapterIntentFilterName)
                    intent.putExtra("endChat", true)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    AppDatabase.instance.messageDao.clearTable()
                    SocketConnections.resetSubscriptions()
                    stopForeground(true)
                    AppDatabase.instance.clearAllTables()
                    stopSelf()
                }
                topicMessage.payload.contains("seen") -> {
                    val seenModel = Gson().fromJson(topicMessage.payload, SeenModel::class.java)
                    if (seenModel.seen){
                        AppDatabase.instance.messageDao.getAll().forEach {
                            it.seen = seenModel.seen
                            AppDatabase.instance.messageDao.update(message = it)
                        }
                    }
                }
                else -> {
                    //получение сообщения
                    val msg = Gson().fromJson(topicMessage.payload, MessageModel::class.java)
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    msg.date = sdf.format(Date())
                    msg.isMine = User.mUuid == msg.author.uuid
                    AppDatabase.instance.messageDao.insert(msg)
                    val intent = Intent(ChatProcessFragment.adapterIntentFilterName)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                    if (!isForeground(this)) {
                        val notificationManager = NotificationManagerCompat.from(this)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channel = NotificationChannel(
                                CHANNEL_ID, "Messages Channel",
                                NotificationManager.IMPORTANCE_HIGH
                            )
                            channel.description = "Messages Channel"
                            channel.enableLights(true)
                            channel.lightColor = Color.RED
                            channel.enableVibration(false)
                            notificationManager.createNotificationChannel(channel)
                        }

                        val pendingIntent = PendingIntent.getActivity(
                            applicationContext,
                            NOTIFICATION_ID,
                            Intent(applicationContext, MainActivity::class.java),
                            PendingIntent.FLAG_IMMUTABLE
                        )

                        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(resources.getString(R.string.anonim))
                            .setContentText(msg.text)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        with(notificationManager) {
                            notify(NOTIFICATION_ID, builder.build()) // посылаем уведомление
                        }
                    } else{
                        val preferencesManager = SharedPreferencesManager(this)
                        preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
                        val rUuid = preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "")
                        SocketConnections.sendStompMessage(
                            "/app/seen.$rUuid",
                            Gson().toJson(
                                SeenModel(true)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun runAsForeground() {
        //создание канала с уведомлениями
        val notificationManager = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID2, "Foreground Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Foreground Channel"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID2)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(getString(R.string.active_chat)) //.setContentIntent(pendingIntent)
            .build()
        startForeground(NOTIFICATION_ID2, notification)
    }

    private fun isForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName: String = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

}