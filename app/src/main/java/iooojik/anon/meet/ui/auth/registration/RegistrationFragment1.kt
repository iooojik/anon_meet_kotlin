package iooojik.anon.meet.ui.auth.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.User
import iooojik.anon.meet.databinding.FragmentRegistration1Binding


class RegistrationFragment1 : Fragment(), RegistrationFragment1Logic {
    private lateinit var binding: FragmentRegistration1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistration1Binding.inflate(inflater)
        setListeners(binding)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.go_to_next_page -> {
                User.mUserLogin = binding.nicknameTextField.editText!!.text.trim().toString()
                User.mPassword = binding.passwordTextField.editText!!.text.trim().toString()
                if (checkNicknameAndPasswordLength(
                        requireView(),
                        resources,
                        binding.passwordTextField.editText!!.text.trim().toString()
                    )
                ) {
                    findNavController().navigate(R.id.action_registrationFragment1_to_registrationFragment2)
                }
            }
        }
    }

}