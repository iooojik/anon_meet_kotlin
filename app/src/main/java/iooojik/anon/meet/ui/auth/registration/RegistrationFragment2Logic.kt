package iooojik.anon.meet.ui.auth.registration

import android.view.View
import iooojik.anon.meet.databinding.FragmentRegistration1Binding
import iooojik.anon.meet.databinding.FragmentRegistration2Binding

interface RegistrationFragment2Logic : View.OnClickListener {
    fun setListeners(binding: FragmentRegistration2Binding) {
        binding.registerButton.setOnClickListener(this)
    }
}