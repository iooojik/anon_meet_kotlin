package iooojik.anon.meet

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import iooojik.anon.meet.net.sockets.StaticSockets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import ua.naiksoftware.stomp.Stomp
import kotlin.concurrent.thread

class WebSocketTest {
    @Test
    fun connectionTest(){
        runBlocking {
            launch {
                val stompClient = Stomp.over(
                    Stomp.ConnectionProvider.OKHTTP,
                    StaticSockets.SOCKET_SSL_URL,
                    null,
                    OkHttpClient.Builder().addInterceptor(
                        HttpLoggingInterceptor()
                    ).build()
                ).withClientHeartbeat(StaticSockets.SERVER_HEARTBEAT)

                stompClient.connect()
                delay(30*1000L)
            }.join()
        }
    }
}