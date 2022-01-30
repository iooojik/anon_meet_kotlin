package iooojik.anon.meet.activity

import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import iooojik.anon.meet.Application
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.data.models.user.UserViewModel
import iooojik.anon.meet.log
import iooojik.anon.meet.net.rest.RetrofitHelper
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import iooojik.anon.meet.showSnackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ActivityMainLogic {

    fun getStartDestination(activity: MainActivity) : Int?{
        val navController = activity.findNavController(R.id.nav_host_fragment)
        val preferencesManager = SharedPreferencesManager(activity.applicationContext)
        preferencesManager.initPreferences()
        if (preferencesManager.getValue(SharedPrefsKeys.USER_TOKEN, "")?.toString()?.trim()?.isNotBlank() == true){
            if (navController.currentDestination?.id == R.id.loginFragment)
                navController.navigate(R.id.action_global_filtersFragment)
        }
        preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
        if (preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "").toString().trim().isNotBlank()){
            navController.navigate(R.id.chatProcessFragment)
        }
        return navController.currentDestination?.id
    }

    fun checkUserTokenAndAuth(activity: MainActivity) {
        val preferencesManager = SharedPreferencesManager(Application.instance().applicationContext)
        preferencesManager.initPreferences()
        if (preferencesManager.getValue(SharedPrefsKeys.USER_TOKEN, "")?.toString()?.trim()?.isNotBlank() == true) {
            authWithUUID(
                token = "${preferencesManager.getValue(SharedPrefsKeys.TOKEN_HEADER, "")} ${preferencesManager.getValue(SharedPrefsKeys.USER_TOKEN, "")}",
                uuid = preferencesManager.getValue(SharedPrefsKeys.USER_UUID, "")!!.toString(),
                activity = activity
            )
        }
    }

    fun setUpToolBar(
        navController: NavController,
        activity: MainActivity
    ) {
        activity.setSupportActionBar(activity.binding.appBarMain.topAppBar)
        activity.setupActionBarWithNavController(
            navController,
            AppBarConfiguration(navController.graph)
        )
    }

    fun authWithUUID(token: String, uuid: String, activity: MainActivity) {
        RetrofitHelper.authService.loginWithUUID(token, User(uuid = uuid))
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        SocketConnections.connectToServer(activity.applicationContext)
                        if (response.body() != null)
                            UserViewModel.changeCurrentUserInfo(response.body()!!)
                    } else {
                        log(response.errorBody().toString(), priority = Log.DEBUG)
                        showSnackbar(
                            activity.binding.root,
                            String.format(
                                activity.resources.getString(R.string.authorization_error),
                                ", ${response.code()}"
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    RetrofitHelper.onFailure(t)
                }

            })
    }

    fun setToolBarMenuClickListener(
        navController: NavController,
        activity: MainActivity
    ) {
        activity.binding.appBarMain.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.go_to_settings -> {
                    navController.navigate(R.id.action_filtersFragment_to_settingsFragment)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }
}