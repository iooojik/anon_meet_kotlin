package iooojik.anon.meet.ui.auth.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.databinding.FragmentRegistration2Binding
import iooojik.anon.meet.net.rest.StaticWeb
import iooojik.anon.meet.openURL
import java.text.SimpleDateFormat
import java.util.*


class RegistrationFragment2 : Fragment(), RegistrationFragment2Logic {
    private lateinit var binding: FragmentRegistration2Binding
    private var birthDate = Date()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistration2Binding.inflate(inflater)
        binding.fragment = this
        return binding.root
    }

    fun registerUser(v: View?) {
        v?.let {
            register(binding, findNavController(), requireActivity())
        }
    }

    fun selectBirthDate(v: View?) {
        v?.let {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                birthDate = Date(it)
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
                User.mBirthDate = formatDate(simpleDateFormat.format(birthDate))
                binding.birthdateTextField.editText!!.setText(simpleDateFormat.format(birthDate))

            }
            datePicker.show(requireActivity().supportFragmentManager, "")
        }
    }

    fun showTermsPolicy(v: View?) {
        v?.let {
            openURL("${StaticWeb.REST_URL}terms_and_policy", requireContext())
        }
    }

}