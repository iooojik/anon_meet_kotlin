package iooojik.anon.meet.ui.filters

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.RangeSlider
import com.google.gson.Gson
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentFiltersBinding
import iooojik.anon.meet.models.StackModel
import iooojik.anon.meet.models.User
import iooojik.anon.meet.models.UserViewModel
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.net.sockets.SocketService
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import ua.naiksoftware.stomp.dto.StompMessage


class FiltersFragment : Fragment(), FiltersFragmentLogic {
    private lateinit var binding: FragmentFiltersBinding
    private val findingBottomSheet = SearchBottomSheet()

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
        Intent(requireActivity(), SocketService::class.java).also { intent ->
            requireActivity().startService(intent)
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.search_button -> {
                findingBottomSheet.show(requireActivity().supportFragmentManager, SearchBottomSheet.TAG)
                val prefs = SharedPreferencesManager(requireContext())
                SocketConnections.connectToServer(prefs.getValue(SharedPrefsKeys.TOKEN_HEADER, "").toString() + " " + prefs.getValue(
                    SharedPrefsKeys.USER_TOKEN,
                    ""
                ).toString())
                SocketConnections.connectToTopic("/topic/${User.mUuid}/find", ::onChatFound)
                SocketConnections.sendStompMessage("/app/find.${User.mUuid}", Gson().toJson(User()))
            }
        }
    }

    private fun onChatFound(topicMessage: StompMessage){
        val prefs = SharedPreferencesManager(requireContext())
        prefs.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
        if(topicMessage.payload.trim().isNotBlank()){
            val foundChatMode = Gson().fromJson(topicMessage.payload, StackModel::class.java)
            prefs.saveValue(SharedPrefsKeys.CHAT_ROOM_UUID, foundChatMode.uuid)
            SocketConnections.resetSubscriptions()
            findingBottomSheet.dismiss()
            findNavController().navigate(R.id.action_filtersFragment_to_chatProcessFragment)
        }
    }

    override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
        when (slider.id) {
            R.id.age_range_slider -> {
                val vals = slider.values
                UserViewModel.changeInterlocutorAges("${vals[0].toInt()}/${vals[1].toInt()}")
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.my_sex_male -> User.mFilter.mySex = if (isChecked) "male" else "female"
            R.id.my_sex_female -> User.mFilter.mySex = if (isChecked) "female" else "male"
            R.id.interlocutor_sex_male ->  if (isChecked)
                User.mFilter.interlocutorSex = "male"
            R.id.interlocutor_sex_female -> if (isChecked)
                User.mFilter.interlocutorSex = "female"
            R.id.interlocutor_sex_nm -> if (isChecked)
                User.mFilter.interlocutorSex = "nm"
        }
    }


}