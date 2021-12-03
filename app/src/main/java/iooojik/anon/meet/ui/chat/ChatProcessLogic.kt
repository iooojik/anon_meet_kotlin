package iooojik.anon.meet.ui.chat

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface ChatProcessLogic {
    fun hideBackButton(activity: AppCompatActivity){
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    fun blockGoBack(activity: ComponentActivity, fragment: Fragment) {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity.finish()
            }
        }
        activity.onBackPressedDispatcher.addCallback(fragment.viewLifecycleOwner, callback)
    }
}