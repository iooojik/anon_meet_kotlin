package iooojik.anon.meet.ui.chat

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import iooojik.anon.meet.activity.MainActivity
import iooojik.anon.meet.databinding.ChatProcessTopBarBinding
import iooojik.anon.meet.databinding.FragmentChatProcessBinding

interface ChatProcessLogic : View.OnClickListener {
    fun setListeners(binding: FragmentChatProcessBinding, topBarBinding: ChatProcessTopBarBinding) {
        binding.mainLayout.messageInputLayout.sendMessage.setOnClickListener(this)
        binding.mainLayout.chatView.setOnClickListener(this)
        topBarBinding.exitChat.setOnClickListener(this)
    }

    fun hideBackButton(activity: AppCompatActivity) {
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    fun blockGoBack(activity: ComponentActivity, fragment: Fragment) {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity.finish()
            }
        }
        activity.onBackPressedDispatcher.addCallback(fragment.viewLifecycleOwner, callback)
    }

    fun hideToolBar(activity: MainActivity){
        activity.supportActionBar?.hide()
    }

    fun showToolBar(activity: MainActivity){
        activity.supportActionBar?.show()
    }

    fun hideKeyBoard(activity: Activity, v: View) {
        val imm: InputMethodManager =
            activity.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}