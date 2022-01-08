package iooojik.anon.meet.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.data.models.user.UserViewModel
import iooojik.anon.meet.databinding.InterlocutorProfileBottomSheetBinding

class InterlocutorProfileBottomSheet(private val interlocutor : User) : BottomSheetDialogFragment(){

    private lateinit var binding: InterlocutorProfileBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InterlocutorProfileBottomSheetBinding.inflate(inflater)
        val provider = ViewModelProvider(this).get(
            UserViewModel::class.java
        )
        interlocutor.userLogin = resources.getString(R.string.anonim)
        provider.userLiveData.value = interlocutor
        binding.profileHeader.user = provider
        return binding.root
    }
}