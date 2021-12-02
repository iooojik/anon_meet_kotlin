package iooojik.anon.meet.ui.filters

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.RangeSlider
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentFiltersBinding
import iooojik.anon.meet.log
import iooojik.anon.meet.models.User
import iooojik.anon.meet.models.UserViewModel


class FiltersFragment : Fragment(), FiltersFragmentLogic {
    private lateinit var binding: FragmentFiltersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersBinding.inflate(inflater)
        binding.lifecycleOwner = this
        hideBackButton(requireActivity() as AppCompatActivity)
        binding.user = ViewModelProvider(this).get(
            UserViewModel::class.java
        )
        blockGoBack(requireActivity(), this)
        setHasOptionsMenu(true)
        setListeners(binding)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onClick(v: View?) {

    }

    override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
        when(slider.id){
            R.id.age_range_slider -> {
                val vals = slider.values
                UserViewModel.changeInterlocutorAges("${vals[0].toInt()}/${vals[1].toInt()}")
            }
        }
    }


}