package iooojik.anon.meet.ui.filters

import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.slider.RangeSlider
import iooojik.anon.meet.databinding.FragmentFiltersBinding

interface FiltersFragmentLogic : View.OnClickListener, RangeSlider.OnChangeListener {
    fun setListeners(binding: FragmentFiltersBinding){
        binding.ageRangeSlider.addOnChangeListener(this)
    }
    fun hideBackButton(activity: AppCompatActivity){
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