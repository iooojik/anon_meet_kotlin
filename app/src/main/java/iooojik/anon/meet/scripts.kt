package iooojik.anon.meet

import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.math.BigInteger
import java.security.MessageDigest

fun log(msg: Any, tag: String = "Programmer log", priority: Int = Log.INFO) {
    Log.println(priority, tag, msg.toString())
}

fun showSnackbar(view: View, content: String) {
    Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show()
}

fun md5(toEncode : String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toEncode.toByteArray())).toString(16).padStart(32, '0')
}