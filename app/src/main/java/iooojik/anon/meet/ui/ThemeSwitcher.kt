package iooojik.anon.meet.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import iooojik.anon.meet.log
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys

interface ThemeSwitcher {
    fun changeTheme(context: Context, isChange: Boolean = true){
        val prefs = SharedPreferencesManager(context)
        prefs.initPreferences()
        if (isChange) {
            if ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                prefs.saveValue(SharedPrefsKeys.THEME_MODE, AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                prefs.saveValue(SharedPrefsKeys.THEME_MODE, AppCompatDelegate.MODE_NIGHT_YES)
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(prefs.getValue(SharedPrefsKeys.THEME_MODE, AppCompatDelegate.MODE_NIGHT_NO)!! as Int)
        }
    }
}