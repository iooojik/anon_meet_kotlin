package iooojik.anon.meet.activity

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.ActivityMainBinding

interface ActivityMainLogic {

    fun setUpToolBar(binding: ActivityMainBinding, navController: NavController) {
        val toolbar = binding.appBarMain.topAppBar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.loginFragment,
            )
        )
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
    }

    fun setToolBarMenuClickListener(
        binding: ActivityMainBinding,
        resources: Resources,
        theme: Resources.Theme
    ) {
        //слушатель на нажатое меню в тулбаре
        getCurrentThemeModeAndSetIcon(binding, resources, theme)
        binding.appBarMain.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dark_mode -> {
                    //переключатель темы приложения
                    it.isChecked = !it.isChecked
                    it.icon = getMenuThemeIcon(it.isChecked, resources, theme)
                    if (it.isChecked)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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