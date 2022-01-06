package iooojik.anon.meet

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import java.math.BigInteger
import java.security.MessageDigest

fun log(msg: Any, tag: String = "Programmer log", priority: Int = Log.INFO) {
    Log.println(priority, tag, msg.toString())
}

fun showSnackbar(view: View, content: String) {
    Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show()
}

fun md5(toEncode: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toEncode.toByteArray())).toString(16).padStart(32, '0')
}

fun hideKeyBoard(activity: Activity, v: View) {
    val imm: InputMethodManager =
        activity.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(v.windowToken, 0)
}
