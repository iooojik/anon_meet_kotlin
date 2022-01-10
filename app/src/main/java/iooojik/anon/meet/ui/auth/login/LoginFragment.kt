package iooojik.anon.meet.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.databinding.FragmentLoginBinding
import iooojik.anon.meet.hideKeyBoard

class LoginFragment : Fragment(), LoginFragmentLogic {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        binding.activity = requireActivity() as AppCompatActivity
        binding.fragment = this
        binding.adBanner.loadAd(AdRequest.Builder().build())
        return binding.root
    }

    fun hideKeyboardAndClearFocus(view: View) {
        hideKeyBoard(requireActivity(), binding.root)
        binding.passwordTextField.clearFocus()
        binding.emailTextField.clearFocus()
    }

    fun onRegistrationButtonClick(view: View) {
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment1)
    }

    fun onLoginButtonClick(view: View) {
        User.mUserLogin = binding.emailTextField.editText!!.text.trim().toString()
        User.mPassword = binding.passwordTextField.editText!!.text.trim().toString()
        if (checkEmailAndPassword(
                requireView(),
                resources,
                binding.passwordTextField.editText!!.text.trim().toString()
            )
        ) {
            auth(findNavController(), requireActivity(), binding)
        }
    }
}