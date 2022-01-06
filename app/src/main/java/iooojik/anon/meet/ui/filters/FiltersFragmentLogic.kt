package iooojik.anon.meet.ui.filters

import android.view.View
import android.widget.CompoundButton
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.slider.RangeSlider
import iooojik.anon.meet.databinding.FragmentFiltersBinding

interface FiltersFragmentLogic : View.OnClickListener, RangeSlider.OnChangeListener,
    CompoundButton.OnCheckedChangeListener {
    fun setListeners(binding: FragmentFiltersBinding) {
        binding.ageRangeSlider.addOnChangeListener(this)
        binding.searchButton.setOnClickListener(this)

        binding.mySexMale.setOnCheckedChangeListener(this)
        binding.mySexFemale.setOnCheckedChangeListener(this)

        binding.interlocutorSexMale.setOnCheckedChangeListener(this)
        binding.interlocutorSexFemale.setOnCheckedChangeListener(this)
        binding.interlocutorSexNm.setOnCheckedChangeListener(this)
    }

    fun hideBackButton(navController: NavController, activity: AppCompatActivity) {
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun blockGoBack(activity: ComponentActivity, fragment: Fragment) {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity.finish()
            }
        }
        activity.onBackPressedDispatcher.addCallback(fragment.viewLifecycleOwner, callback)
    }

}