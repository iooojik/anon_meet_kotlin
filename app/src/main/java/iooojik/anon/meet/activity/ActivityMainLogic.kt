package iooojik.anon.meet.activity

import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.ActivityMainBinding

open class ActivityMainLogic {

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

        binding.appBarMain.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dark_mode -> {
                    //переключатель темы приложения
                    it.isChecked = !it.isChecked
                    it.icon = if (it.isChecked) ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.outline_light_mode_24,
                        theme
                    ) else
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.outline_dark_mode_24,
                            theme
                        )
                    if (it.isChecked)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }
}