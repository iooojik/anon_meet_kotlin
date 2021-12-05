package iooojik.anon.meet.ui.auth.registration

import android.view.View
import iooojik.anon.meet.databinding.FragmentRegistration1Binding
import iooojik.anon.meet.ui.auth.login.LoginFragmentLogic

interface RegistrationFragment1Logic : View.OnClickListener, LoginFragmentLogic {
    fun setListeners(binding: FragmentRegistration1Binding) {
        binding.goToNextPage.setOnClickListener(this)
    }
}