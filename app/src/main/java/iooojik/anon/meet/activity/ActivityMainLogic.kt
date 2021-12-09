package iooojik.anon.meet.activity

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.User
import iooojik.anon.meet.data.models.UserViewModel
import iooojik.anon.meet.databinding.ActivityMainBinding
import iooojik.anon.meet.log
import iooojik.anon.meet.net.rest.RetrofitHelper
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import iooojik.anon.meet.ui.ConfirmationBottomSheet
import iooojik.anon.meet.ui.ThemeSwitcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ActivityMainLogic : ThemeSwitcher {

    fun checkUserTokenAndAuth(
        context: Context,
        navController: NavController
    ) {
        val preferencesManager = SharedPreferencesManager(context)
        preferencesManager.initPreferences()
        if (preferencesManager.getValue(
                SharedPrefsKeys.USER_TOKEN,
                ""
            ) != null && preferencesManager.getValue(SharedPrefsKeys.USER_TOKEN, "")?.toString()
                ?.trim()?.isNotBlank() == true
        ) {
            authWithUUID(
                "${
                    preferencesManager.getValue(
                        SharedPrefsKeys.TOKEN_HEADER,
                        ""
                    )
                } ${
                    preferencesManager.getValue(
                        SharedPrefsKeys.USER_TOKEN,
                        ""
                    )
                }", preferencesManager.getValue(
                    SharedPrefsKeys.USER_UUID,
                    ""
                )!!.toString()
            )
            log("${navController.currentDestination?.id} ${R.id.filtersFragment}")
            if (navController.currentDestination?.id != R.id.filtersFragment)
                navController.navigate(R.id.action_global_filtersFragment)

            preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
            if (preferencesManager.getValue(
                    SharedPrefsKeys.CHAT_ROOM_UUID,
                    ""
                ) != null && preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "")
                    .toString().trim().isNotBlank()
            ) {
                navController.navigate(R.id.chatProcessFragment)
            }
        }
    }

    fun setUpToolBar(
        binding: ActivityMainBinding,
        navController: NavController,
        activity: AppCompatActivity
    ) {
        activity.setSupportActionBar(binding.appBarMain.topAppBar)
        activity.setupActionBarWithNavController(
            navController,
            AppBarConfiguration(navController.graph)
        )
    }

    fun authWithUUID(token: String, uuid: String) {
        RetrofitHelper.authService.loginWithUUID(token, User(uuid = uuid))
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        if (response.body() != null)
                            UserViewModel.changeUserInfo(response.body()!!)
                    } else log(response.errorBody().toString())
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    RetrofitHelper.onFailure(t)
                }

            })
    }

    fun setToolBarMenuClickListener(
        binding: ActivityMainBinding,
        resources: Resources,
        theme: Resources.Theme,
        navController: NavController,
        activity: AppCompatActivity
    ) {
        //слушатель на нажатое меню в тулбаре
        getCurrentThemeModeAndSetIcon(binding, resources, theme)
        binding.appBarMain.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dark_mode -> {
                    changeTheme(activity.applicationContext)
                    //переключатель темы приложения
                    //it.isChecked = !it.isChecked
                    //it.icon = getMenuThemeIcon(it.isChecked, resources, theme)
                    //if (it.isChecked)
                    //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    //else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                R.id.go_to_settings -> {
                    navController.navigate(R.id.action_filtersFragment_to_settingsFragment)
                }
                R.id.finish_chat -> {
                    ConfirmationBottomSheet(message = resources.getString(R.string.finish_chat_confirmation)) {
                        val prefs = SharedPreferencesManager(activity.applicationContext)
                        prefs.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
                        SocketConnections.sendStompMessage(
                            "/app/end.chat.${
                                prefs.getValue(
                                    SharedPrefsKeys.CHAT_ROOM_UUID,
                                    ""
                                ).toString()
                            }", Gson().toJson(User())
                        )
                        prefs.clearAll()

                    }.show(activity.supportFragmentManager, ConfirmationBottomSheet.TAG)

                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun getCurrentThemeModeAndSetIcon(
        binding: ActivityMainBinding,
        resources: Resources,
        theme: Resources.Theme
    ) {
        val switchMode = binding.appBarMain.topAppBar.menu.findItem(R.id.dark_mode)
        switchMode.isChecked =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        switchMode.icon = getMenuThemeIcon(switchMode.isChecked, resources, theme)
    }

    private fun getMenuThemeIcon(
        isChecked: Boolean,
        resources: Resources,
        theme: Resources.Theme
    ): Drawable = if (isChecked) ResourcesCompat.getDrawable(
        resources,
        R.drawable.outline_light_mode_24,
        theme
    )!! else
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.outline_dark_mode_24,
            theme
        )!!
}