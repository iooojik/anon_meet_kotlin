package iooojik.anon.meet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import java.math.BigInteger
import java.security.MessageDigest
import java.util.regex.Pattern

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

fun hideKeyBoard(activity: Activity? = null, v: View, context: Context? = null) {
    val c = if (activity != null) activity.applicationContext else context
    if (c != null) {
        val imm: InputMethodManager =
            c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun openURL(url: String, context: Context) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(browserIntent)
}

fun isEmail(toCheck: String): Boolean {
    val emailRegex =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    return if (toCheck.isBlank()) false
    else return Pattern.compile(emailRegex).matcher(toCheck).matches()
}

fun getLoginFromEmail(email: String) =
    email.replace(Regex("@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}\$"), "")

