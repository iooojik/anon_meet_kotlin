package iooojik.anon.meet.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.search.SearchStateModel
import iooojik.anon.meet.data.models.search.SearchStateViewModel
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.data.models.user.UserViewModel
import iooojik.anon.meet.databinding.SearchBottomSheetLayoutBinding
import iooojik.anon.meet.net.sockets.SocketConnections
import okhttp3.internal.notify

class SearchBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: SearchBottomSheetLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchBottomSheetLayoutBinding.inflate(inflater)

        val provider = ViewModelProvider(this).get(
            SearchStateViewModel::class.java
        )
        searchModelProvider = provider
        searchModelProvider!!.data.observe(this, {
            it?.let {
                binding.searchModel = it
            }
        })
        binding.searchModel = searchModelProvider!!.data.value

        binding.cancelSearchButton.setOnClickListener(this)
        binding.adBanner.loadAd(AdRequest.Builder().build())
        isCancelable = false
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cancel_search_button -> {
                SocketConnections.sendStompMessage(
                    "/app/cancel.chat.${User.mUuid}",
                    Gson().toJson(User())
                )
                dismiss()
            }
        }
    }

    companion object {
        val TAG = SearchBottomSheet::javaClass.name
        var searchModelProvider: SearchStateViewModel? = null
    }
}