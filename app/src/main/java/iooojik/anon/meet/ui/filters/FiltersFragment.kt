package iooojik.anon.meet.ui.filters

import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.RangeSlider
import com.google.gson.Gson
import iooojik.anon.meet.AdUtil
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.search.SearchStateModel
import iooojik.anon.meet.data.models.search.SearchStateViewModel
import iooojik.anon.meet.data.models.search.StackModel
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.data.models.user.UserViewModel
import iooojik.anon.meet.databinding.FragmentFiltersBinding
import iooojik.anon.meet.log
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import ua.naiksoftware.stomp.dto.StompMessage


class FiltersFragment : Fragment(), FiltersFragmentLogic {
    private lateinit var binding: FragmentFiltersBinding
    private val findingBottomSheet = SearchBottomSheet()

    companion object {
        var tapCounter = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersBinding.inflate(inflater)
        binding.lifecycleOwner = this
        hideBackButton(findNavController(), requireActivity() as AppCompatActivity)
        checkAppFirstStartUp()
        binding.user = ViewModelProvider(this).get(
            UserViewModel::class.java
        )
        binding.adBanner.loadAd(AdRequest.Builder().build())
        blockGoBack(requireActivity(), this)
        setHasOptionsMenu(true)
        setListeners(binding)
        return binding.root
    }

    private fun checkAppFirstStartUp() {
        val prefs = SharedPreferencesManager(requireContext())
        prefs.initPreferences()
        val v = prefs.getValue(SharedPrefsKeys.FIRST_START_UP, true)
        if (v is Boolean && v){
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.first_start_up_title))
                .setMessage(resources.getString(R.string.first_start_up_message))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.okey)) { dialog, _ ->
                    prefs.saveValue(SharedPrefsKeys.FIRST_START_UP, false)
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.search_button -> {
                tapCounter++
                if (tapCounter % 3 == 0){
                    AdUtil.showInterstitialAd(requireActivity())
                    tapCounter = 0
                } else {
                    findingBottomSheet.show(
                        requireActivity().supportFragmentManager,
                        SearchBottomSheet.TAG
                    )
                    SocketConnections.connectToServer(requireContext())
                    SocketConnections.connectToTopic("/topic/${User.mUuid}/find", ::onChatFound)
                    SocketConnections.sendStompMessage("/app/find.${User.mUuid}", Gson().toJson(User()))
                }
            }
        }
    }

    private fun onChatFound(topicMessage: StompMessage) {
        val prefs = SharedPreferencesManager(requireContext())
        prefs.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
        if (topicMessage.payload.trim().isNotBlank() && topicMessage.payload.contains("inSearchUsers")){
            val searchStateModel = Gson().fromJson(topicMessage.payload, SearchStateModel::class.java)!!
            log(searchStateModel.inSearchUsers)
            SearchBottomSheet.searchModelProvider?.data?.value = searchStateModel

        } else if (topicMessage.payload.trim().isNotBlank()) {
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
                UserViewModel.changeCurrentInterlocutorAges("${vals[0].toInt()}/${vals[1].toInt()}")
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.my_sex_male -> User.mFilter.mySex = if (isChecked) "male" else "female"
            R.id.my_sex_female -> User.mFilter.mySex = if (isChecked) "female" else "male"
            R.id.interlocutor_sex_male -> if (isChecked)
                User.mFilter.interlocutorSex = "male"
            R.id.interlocutor_sex_female -> if (isChecked)
                User.mFilter.interlocutorSex = "female"
            R.id.interlocutor_sex_nm -> if (isChecked)
                User.mFilter.interlocutorSex = "nm"
        }
    }


}