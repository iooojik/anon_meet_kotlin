package iooojik.anon.meet.net.sockets

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import iooojik.anon.meet.R
import iooojik.anon.meet.log
import iooojik.anon.meet.models.MessageModel
import iooojik.anon.meet.models.TypingModel
import iooojik.anon.meet.models.User
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import iooojik.anon.meet.ui.chat.ChatProcessFragment
import iooojik.anon.meet.ui.chat.MessagesViewModel
import ua.naiksoftware.stomp.dto.StompMessage
import java.text.SimpleDateFormat
import java.util.*


class SocketService : Service() {

    private lateinit var preferencesManager: SharedPreferencesManager
    private var roomUuid = ""

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
            log(topicMessage)
            when {
                topicMessage.payload.contains("typing") -> {
                    val typingModel = Gson().fromJson(topicMessage.payload, TypingModel::class.java)
                    TypingModel.mTyping = typingModel.typing
                    TypingModel.mTypingUser = typingModel.typingUser
                    val intent = Intent(ChatProcessFragment.adapterIntentFilterName)
                    intent.putExtra("typing", true)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                }
                topicMessage.payload.contains("endChat") -> {
                    preferencesManager = SharedPreferencesManager(this)
                    preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
                    preferencesManager.clearAll()
                    val intent = Intent(ChatProcessFragment.adapterIntentFilterName)
                    intent.putExtra("endChat", true)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    SocketConnections.resetSubscriptions()
                    stopSelf()
                    stopForeground(true)
                }
                else -> {
                    val msg = Gson().fromJson(topicMessage.payload, MessageModel::class.java)
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    msg.date = sdf.format(Date())
                    msg.isMine = User.mUuid == msg.author.uuid
                    MessagesViewModel.messages.add(msg)
                    val intent = Intent(ChatProcessFragment.adapterIntentFilterName)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    log(isForeground(this))
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

                        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(msg.author.userLogin)
                            .setContentText(msg.text)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        with(notificationManager) {
                            notify(NOTIFICATION_ID, builder.build()) // посылаем уведомление
                        }
                    }


                }
            }
        }
    }

    private fun runAsForeground() {
        //Intent notificationIntent = new Intent(this, RecorderMainActivity.class);
        //PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
        //notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        val notificationManager = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID2, "Foreground Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Foreground Channel"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID2)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(getString(R.string.app_name)) //.setContentIntent(pendingIntent)
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