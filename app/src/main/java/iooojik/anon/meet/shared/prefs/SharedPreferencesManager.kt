package iooojik.anon.meet.shared.prefs

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(private val activity: Activity) {
    private val preferencesName = "${activity.packageName}.preferences"
    private var preferences: SharedPreferences? = null

    fun initPreferences(prefsName: String = preferencesName, mode: Int = Context.MODE_PRIVATE) {
        preferences = activity.getSharedPreferences(prefsName, mode)
    }

    fun getValue(key: String, defValue: Any): Any? {
        if (preferences == null) initPreferences()
        return when (defValue) {
            is Boolean -> preferences!!.getBoolean(key, defValue as Boolean)
            is String ->
                preferences!!.getString(key, defValue as String)
            is Float -> preferences!!.getFloat(key, defValue as Float)
            is Int -> preferences!!.getInt(key, defValue as Int)
            is Long -> preferences!!.getLong(key, defValue as Long)
            else -> preferences!!.all
        }
    }

    fun saveValue(key: String, value: Any): Boolean {
        if (preferences == null) initPreferences()
        var r = true

        when (value) {
            is Boolean -> preferences!!.edit().putBoolean(key, value as Boolean).apply()
            is String ->
                preferences!!.edit().putString(key, value as String).apply()
            is Float -> preferences!!.edit().putFloat(key, value as Float).apply()
            is Int -> preferences!!.edit().putInt(key, value as Int).apply()
            is Long -> preferences!!.edit().putLong(key, value as Long).apply()
            else -> r = false
        }
        return r
    }
}