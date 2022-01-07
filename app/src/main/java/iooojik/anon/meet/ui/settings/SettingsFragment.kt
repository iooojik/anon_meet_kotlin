package iooojik.anon.meet.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import iooojik.anon.meet.AdUtil
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.UserViewModel
import iooojik.anon.meet.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment(), SettingsFragmentLogic {
    lateinit var binding: FragmentSettingsBinding

    companion object {
        var windowOpenedCounter = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)
        val provider = ViewModelProvider(this).get(
            UserViewModel::class.java
        )
        provider.userLiveData.value = UserViewModel.currentUser.value
        binding.profileHeader.user = provider
        binding.fragment = this
        binding.adBanner.loadAd(AdRequest.Builder().build())
        setListeners(binding, resources)
        return binding.root
    }

    fun goToAppInfo(view: View?){
        findNavController().navigate(R.id.action_settingsFragment_to_aboutAppFragment)
    }

    override fun onResume() {
        super.onResume()
        windowOpenedCounter++
        if (windowOpenedCounter % 5 == 0) {
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