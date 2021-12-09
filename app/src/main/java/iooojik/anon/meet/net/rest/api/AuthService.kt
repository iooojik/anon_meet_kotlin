package iooojik.anon.meet.net.rest.api

import iooojik.anon.meet.data.models.LoginResponse
import iooojik.anon.meet.data.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthService {

    @POST("auth/login/")
    fun login(@Body user: User): Call<LoginResponse>

    @POST("auth/registration/")
    fun registration(@Body user: User): Call<User>

    @POST("auth/uuid.login/")
    fun loginWithUUID(@Header("Authorization") token: String, @Body user: User): Call<User>

}