package iooojik.anon.meet.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import iooojik.anon.meet.AdUtil
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.UserViewModel
import iooojik.anon.meet.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment(), SettingsFragmentLogic {
    private lateinit var binding: FragmentSettingsBinding

    companion object {
        var windowOpenedCounter = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)
        binding.user = ViewModelProvider(this).get(
            UserViewModel::class.java
        )
        binding.adBanner.loadAd(AdRequest.Builder().build())
        setListeners(binding, resources)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        windowOpenedCounter++
        if (windowOpenedCounter % 2 == 0) {
            windowOpenedCounter = 0
            AdUtil.loadInterstitialAd(requireActivity(), true)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.theme_change_switch -> {
                changeTheme(requireContext())
            }
        }
    }

}