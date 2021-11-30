package iooojik.anon.meet.ui.auth.login

import android.view.View
import iooojik.anon.meet.databinding.FragmentLoginBinding

interface LoginFragmentLogic : View.OnClickListener{
    fun setListeners(binding: FragmentLoginBinding){
        binding.goToRegistrationButton.setOnClickListener(this)
    }
}