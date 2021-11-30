package iooojik.anon.meet.ui.auth.registration

import android.view.View
import iooojik.anon.meet.databinding.FragmentRegistration1Binding

interface RegistrationFragment1Logic : View.OnClickListener {
    fun setListeners(binding: FragmentRegistration1Binding) {
        binding.goToNextPage.setOnClickListener(this)
    }
}