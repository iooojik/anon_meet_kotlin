package iooojik.anon.meet.ui.auth.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentRegistration2Binding
import iooojik.anon.meet.models.User
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
        setListeners(binding)
        return binding.root
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.select_birth_date -> {
                val datePicker = MaterialDatePicker.Builder.datePicker().build()
                datePicker.addOnPositiveButtonClickListener {
                    birthDate = Date(it)
                    val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
                    User.mBirthDate = formatDate(simpleDateFormat.format(birthDate))
                    binding.birthdateTextField.editText!!.setText(simpleDateFormat.format(birthDate))

                }
                datePicker.show(requireActivity().supportFragmentManager, "")
            }
            R.id.register_button -> {
                register(binding, findNavController(), requireActivity())
            }
        }
    }



}