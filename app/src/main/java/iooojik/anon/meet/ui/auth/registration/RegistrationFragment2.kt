package iooojik.anon.meet.ui.auth.registration

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentRegistration1Binding
import iooojik.anon.meet.databinding.FragmentRegistration2Binding
import iooojik.anon.meet.log


class RegistrationFragment2 : Fragment(), RegistrationFragment2Logic {
    private lateinit var binding: FragmentRegistration2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistration2Binding.inflate(inflater)
        setListeners(binding)
        return binding.root
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.register_button -> {
                findNavController().navigate(R.id.action_global_filtersFragment)
            }
        }
    }

}