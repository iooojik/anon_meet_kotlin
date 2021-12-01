package iooojik.anon.meet.ui.auth.registration

import android.view.View
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentRegistration1Binding
import iooojik.anon.meet.models.LoginResponse
import iooojik.anon.meet.models.User
import iooojik.anon.meet.net.rest.RetrofitHelper
import iooojik.anon.meet.ui.auth.login.LoginFragmentLogic
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface RegistrationFragment1Logic : View.OnClickListener, LoginFragmentLogic {
    fun setListeners(binding: FragmentRegistration1Binding) {
        binding.goToNextPage.setOnClickListener(this)
    }
}