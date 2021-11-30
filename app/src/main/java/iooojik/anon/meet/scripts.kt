package iooojik.anon.meet

import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun log(msg: Any, priority: Int = Log.INFO) {
    Log.println(priority, "Programmer log", msg.toString())
}

fun showSnackbar(view: View, content: String) {
    Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show()
}