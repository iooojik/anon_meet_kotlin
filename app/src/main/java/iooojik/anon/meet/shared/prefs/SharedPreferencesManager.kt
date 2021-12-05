package iooojik.anon.meet.shared.prefs

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(private val context: Context) {
    private val preferencesName = "${context.packageName}.preferences"
    private var preferences: SharedPreferences? = null

    fun initPreferences(prefsName: String = preferencesName, mode: Int = Context.MODE_PRIVATE) {
        preferences = context.getSharedPreferences(prefsName, mode)
    }

    fun getValue(key: String, defValue: Any): Any? {
        if (preferences == null) initPreferences()
        return when (defValue) {
            is Boolean -> preferences!!.getBoolean(key, defValue)
            is String ->
                preferences!!.getString(key, defValue)
            is Float -> preferences!!.getFloat(key, defValue)
            is Int -> preferences!!.getInt(key, defValue)
            is Long -> preferences!!.getLong(key, defValue)
            else -> preferences!!.all
        }
    }

    fun saveValue(key: String, value: Any): Boolean {
        if (preferences == null) initPreferences()
        var r = true

        when (value) {
            is Boolean -> preferences!!.edit().putBoolean(key, value).apply()
            is String ->
                preferences!!.edit().putString(key, value).apply()
            is Float -> preferences!!.edit().putFloat(key, value).apply()
            is Int -> preferences!!.edit().putInt(key, value).apply()
            is Long -> preferences!!.edit().putLong(key, value).apply()
            else -> r = false
        }
        return r
    }

    fun clearAll() {
        preferences?.edit()?.clear()?.apply()
    }
}