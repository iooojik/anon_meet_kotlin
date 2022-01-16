package iooojik.anon.meet.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iooojik.anon.meet.AdUtil
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.user.UserViewModelProvider
import iooojik.anon.meet.databinding.FragmentSettingsBinding
import iooojik.anon.meet.getLoginFromEmail
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager


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

        UserViewModelProvider(this).liveData.observe(this, {
            it?.let {
                it.userLogin = getLoginFromEmail(it.userLogin)
                binding.profileHeader.user = it
            }
        })

        binding.fragment = this
        setListeners(binding, resources)
        return binding.root
    }

    fun goToAppInfo(view: View?) {
        view?.let {
            findNavController().navigate(R.id.action_settingsFragment_to_aboutAppFragment)
        }
    }

    fun exitFromAccount(view: View?) {
        view?.let {
            val prefManager = SharedPreferencesManager(requireContext())
            prefManager.initPreferences()
            prefManager.clearAll()
            findNavController().navigate(R.id.action_global_auth_navigation)
        }
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