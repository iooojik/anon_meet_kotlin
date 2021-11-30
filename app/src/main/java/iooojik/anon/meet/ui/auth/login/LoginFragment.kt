package iooojik.anon.meet.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentLoginBinding

class LoginFragment : Fragment(), LoginFragmentLogic {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        setListeners(binding)
        return binding.root
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.go_to_registration_button -> {
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment1)
            }
        }
    }
}