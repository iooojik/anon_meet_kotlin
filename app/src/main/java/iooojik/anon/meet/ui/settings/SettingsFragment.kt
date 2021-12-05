package iooojik.anon.meet.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import iooojik.anon.meet.R
import iooojik.anon.meet.activity.MainActivity
import iooojik.anon.meet.databinding.FragmentSettingsBinding
import iooojik.anon.meet.models.UserViewModel


class SettingsFragment : Fragment(), SettingsFragmentLogic {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)
        binding.user = ViewModelProvider(this).get(
            UserViewModel::class.java
        )
        setListeners(binding, resources)
        return binding.root
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.theme_change_switch -> {
                changeTheme(requireContext())
            }
        }
    }

}