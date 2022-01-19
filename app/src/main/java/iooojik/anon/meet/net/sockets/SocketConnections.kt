package iooojik.anon.meet.net.sockets

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import iooojik.anon.meet.log
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage


class SocketConnections {
    companion object {
        @JvmStatic
        lateinit var stompClient: StompClient

        @JvmStatic
        private var compositeDisposable = CompositeDisposable()

        @JvmStatic
        val topics = mutableMapOf<String, Disposable>()

        fun connectToServer(context: Context) {
            if (!(this::stompClient.isInitialized && this.stompClient.isConnected)) {
                disconnect()
                resetSubscriptions()
                val prefs = SharedPreferencesManager(context)
                val token = prefs.getValue(SharedPrefsKeys.TOKEN_HEADER, "")
                    .toString() + " " + prefs.getValue(SharedPrefsKeys.USER_TOKEN, "").toString()
                stompClient = Stomp.over(
                    Stomp.ConnectionProvider.OKHTTP,
                    StaticSockets.SOCKET_SSL_URL,
                    mapOf("Authorization" to token),
                    OkHttpClient.Builder().addInterceptor(
                        HttpLoggingInterceptor()
                    ).build()
                ).withClientHeartbeat(StaticSockets.SERVER_HEARTBEAT)
                val disposable = stompClient.lifecycle().subscribe({ lifecycleEvent ->
                    when (lifecycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> log("Stomp connection opened")
                        LifecycleEvent.Type.ERROR -> log("Stomp connection error ${lifecycleEvent.exception}")
                        LifecycleEvent.Type.CLOSED -> log("Stomp connection closed")
                        else -> {

                        }
                    }

                }, { throwable -> //log("Error on subscribe topic $throwable")
                    throw throwable
                    //throwable.printStackTrace()
                }, {

                }
                )
                compositeDisposable.add(disposable)
                stompClient.connect()
            }
        }

        fun connectToTopic(path: String, onSubscribeFun: (topicMessage: StompMessage) -> Unit) {
            val disposableTopic: Disposable = stompClient.topic(path.replace("//", "/"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    it.printStackTrace()
                }
                .subscribe({ topicMessage ->
                    onSubscribeFun(topicMessage)
                }, { throwable ->
                    throw throwable
                    //throwable.printStackTrace()
                    //log(throwable)
                }, {
                    log("error")
                })
            compositeDisposable.add(disposableTopic)
            topics[path] = disposableTopic
        }

        fun sendStompMessage(path: String, body: String) {
            stompClient.send(path, body).subscribe()
        }

        private fun disconnect() {
            resetSubscriptions()
            if (this::stompClient.isInitialized)
                stompClient.disconnect()
        }

        fun resetSubscriptions() {
            compositeDisposable.dispose()
            compositeDisposable = CompositeDisposable()
        }
    }
}