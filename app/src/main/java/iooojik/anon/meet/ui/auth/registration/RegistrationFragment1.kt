package iooojik.anon.meet.ui.auth.registration

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.databinding.FragmentRegistration1Binding
import iooojik.anon.meet.hideKeyBoard


class RegistrationFragment1 : Fragment(), RegistrationFragment1Logic {
    private lateinit var binding: FragmentRegistration1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistration1Binding.inflate(inflater)
        binding.fragment = this
        setNicknameFilter()
        return binding.root
    }

    private fun setNicknameFilter() {
        binding.nicknameTextField.editText!!.filters =
            arrayOf(InputFilter { src, _, _, _, _, _ ->
                if (src.equals("")) {
                    return@InputFilter src
                }
                if (src.toString().matches("[a-zA-Z0-9]+".toRegex())) {
                    return@InputFilter src
                }
                return@InputFilter ""
            }
            )
    }

    fun goToNextStep(view: View) {
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

    fun onLayoutClick(view: View) {
        hideKeyBoard(requireActivity(), binding.root)
        binding.passwordTextField.clearFocus()
        binding.nicknameTextField.clearFocus()
    }

}