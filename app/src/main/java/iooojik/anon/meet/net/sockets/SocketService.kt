package iooojik.anon.meet.net.sockets

import android.app.Service
import android.content.Intent
import android.os.IBinder
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys

class SocketService : Service() {

    override fun onCreate() {
        val prefs = SharedPreferencesManager(this)
        SocketConnections.connectToServer(
            prefs.getValue(SharedPrefsKeys.TOKEN_HEADER, "").toString() + " " + prefs.getValue(SharedPrefsKeys.USER_TOKEN,
                ""
            ).toString()
        )

        super.onCreate()
    }

    override fun onDestroy() {
        //alarm manager с проверкой сообщений
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}