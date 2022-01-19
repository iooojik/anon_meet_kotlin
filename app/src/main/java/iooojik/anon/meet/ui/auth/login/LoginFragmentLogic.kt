package iooojik.anon.meet.ui.auth.login

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.navigation.NavController
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.LoginResponse
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.data.models.user.UserViewModel
import iooojik.anon.meet.databinding.FragmentLoginBinding
import iooojik.anon.meet.isEmail
import iooojik.anon.meet.net.rest.RetrofitHelper
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import iooojik.anon.meet.showSnackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface LoginFragmentLogic {

    fun auth(navController: NavController, activity: Activity, binding: FragmentLoginBinding?) {
        binding?.progressBar?.visibility = View.VISIBLE
        RetrofitHelper.authService.login(User()).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding?.progressBar?.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        setUserInfoFromResponse(
                            response = response,
                            context = activity.applicationContext
                        )
                        if (navController.currentDestination?.id == R.id.loginFragment)
                            navController.navigate(R.id.action_global_filtersFragment)
                    }
                } else if (binding != null) RetrofitHelper.onUnsuccessfulResponse(
                    binding.root,
                    response.errorBody(),
                    activity
                )
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                if (binding != null) {
                    RetrofitHelper.onFailure(t, binding.root, activity)
                } else RetrofitHelper.onFailure(t)
                binding?.progressBar?.visibility = View.INVISIBLE
            }

        })
    }

    fun setUserInfoFromResponse(response: Response<LoginResponse>, context: Context) {
        if (response.body() is LoginResponse) {
            val body = response.body() as LoginResponse
            if (response.body() != null)
                UserViewModel.changeCurrentUserInfo(body.user)

            val prefsManager = SharedPreferencesManager(context)
            prefsManager.initPreferences()
            prefsManager.saveValue(SharedPrefsKeys.USER_TOKEN, body.tokenData.token)
            prefsManager.saveValue(
                SharedPrefsKeys.TOKEN_HEADER,
                body.tokenData.tokenHeader
            )
            prefsManager.saveValue(SharedPrefsKeys.USER_UUID, User.mUuid)
        }
    }

    fun checkEmailAndPassword(
        view: View,
        resources: Resources,
        originalPassword: String
    ): Boolean {
        var r = true
        when {
            !isEmail(User.mUserLogin.trim()) -> {
                showSnackbar(
                    view,
                    resources.getString(R.string.not_acceptable_email)
                )
                r = false
            }
            originalPassword.length < 6 -> {
                showSnackbar(
                    view,
                    resources.getString(R.string.short_password)
                )
                r = false
            }
        }
        return r
    }
}