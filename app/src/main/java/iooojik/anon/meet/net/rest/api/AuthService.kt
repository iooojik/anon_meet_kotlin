package iooojik.anon.meet.net.rest.api

import iooojik.anon.meet.models.LoginResponse
import iooojik.anon.meet.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {

    @POST("auth/login/")
    fun login(@Body user: User) : Call<LoginResponse>

    @POST("auth/registration/")
    fun registration(@Body user: User) : Call<User>

}