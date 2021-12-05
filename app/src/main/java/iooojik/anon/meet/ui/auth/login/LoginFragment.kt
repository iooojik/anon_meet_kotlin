package iooojik.anon.meet.ui.auth.login

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentLoginBinding
import iooojik.anon.meet.models.User

class LoginFragment : Fragment(), LoginFragmentLogic {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        setListeners(binding)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_auth_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.go_to_registration_button -> {
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment1)
            }
            R.id.login_Button -> {
                User.mUserLogin = binding.nicknameTextField.editText!!.text.trim().toString()
                User.mPassword = binding.passwordTextField.editText!!.text.trim().toString()
                if (checkNicknameAndPasswordLength(
                        requireView(),
                        resources,
                        binding.passwordTextField.editText!!.text.trim().toString()
                    )
                ) {
                    auth(findNavController(), requireActivity(), binding)
                }
            }
        }
    }
}