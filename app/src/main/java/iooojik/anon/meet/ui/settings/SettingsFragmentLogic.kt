package iooojik.anon.meet.ui.settings

import android.content.res.Configuration
import android.content.res.Resources
import android.widget.CompoundButton
import iooojik.anon.meet.databinding.FragmentSettingsBinding
import iooojik.anon.meet.ui.ThemeSwitcher

interface SettingsFragmentLogic : CompoundButton.OnCheckedChangeListener, ThemeSwitcher {
    fun setListeners(binding: FragmentSettingsBinding, resources: Resources) {
        binding.themeChangeSwitch.isChecked =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.themeChangeSwitch.setOnCheckedChangeListener(this)
    }
}