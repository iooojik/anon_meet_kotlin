package iooojik.anon.meet.net.rest


import android.app.Activity
import android.content.ContentValues.TAG
import android.view.View
import com.google.android.material.snackbar.Snackbar
import iooojik.anon.meet.R
import iooojik.anon.meet.log
import iooojik.anon.meet.net.rest.api.AuthService
import iooojik.anon.meet.showSnackbar
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitHelper {

    companion object {
        @JvmStatic
        lateinit var retrofit: Retrofit

        @JvmStatic
        lateinit var authService: AuthService

        @JvmStatic
        fun doRetrofit() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
            retrofit = Retrofit.Builder()
                .baseUrl(StaticWeb.REST_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            authService = retrofit.create(AuthService::class.java)
        }


        fun onUnsuccessfulResponse(
            view: View?,
            response: ResponseBody?,
            activity: Activity? = null
        ) {
            //обработчик ошибок запроса
            try {
                if (view != null) {
                    if (response != null) {
                        val jsonError = JSONObject(response.string())
                        activity?.runOnUiThread {
                            Snackbar.make(
                                view,
                                jsonError.getString("errorMessage").toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        log(response.string(), TAG)
                    }
                } else if (response != null) {
                    log(JSONObject(response.string()).getString("errorMessage").toString(), TAG)
                }
            } catch (e: Exception) {
                log(e, TAG)
            }
        }

        fun onFailure(t: Throwable, view: View? = null, activity: Activity? = null) {
            log("FAILURE $t", TAG)
            if (activity != null && view != null){
                showSnackbar(view, activity.resources.getString(R.string.error_request))
            }
        }

    }


}

