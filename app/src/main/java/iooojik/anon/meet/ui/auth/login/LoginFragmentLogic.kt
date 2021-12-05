package iooojik.anon.meet.ui.auth.login

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.navigation.NavController
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentLoginBinding
import iooojik.anon.meet.log
import iooojik.anon.meet.models.LoginResponse
import iooojik.anon.meet.models.User
import iooojik.anon.meet.models.UserViewModel
import iooojik.anon.meet.net.rest.RetrofitHelper
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import iooojik.anon.meet.showSnackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface LoginFragmentLogic : View.OnClickListener {
    fun setListeners(binding: FragmentLoginBinding) {
        binding.goToRegistrationButton.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)
    }

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
                        navController.navigate(R.id.action_global_filtersFragment)
                    }
                } else if (binding != null) RetrofitHelper.onUnsuccessfulResponse(
                    binding.root,
                    response.errorBody(),
                    activity
                ) else log(response.errorBody().toString())
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
                UserViewModel.changeUserInfo(body.user)

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

    fun checkNicknameAndPasswordLength(
        view: View,
        resources: Resources,
        originalPassword: String
    ): Boolean {
        var r = true
        when {
            User.mUserLogin.trim().length < 6 -> {
                showSnackbar(
                    view,
                    resources.getString(R.string.short_nickname)
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