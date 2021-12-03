package iooojik.anon.meet.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.SearchBottomSheetLayoutBinding
import iooojik.anon.meet.models.User
import iooojik.anon.meet.net.sockets.SocketConnections

class SearchBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: SearchBottomSheetLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchBottomSheetLayoutBinding.inflate(inflater)
        binding.cancelSearchButton.setOnClickListener(this)
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
    }
}