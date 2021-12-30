package iooojik.anon.meet.ui.chat

import android.view.View
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
}