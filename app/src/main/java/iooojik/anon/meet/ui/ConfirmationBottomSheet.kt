package iooojik.anon.meet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.ConfirmationBottomSheetLayoutBinding

class ConfirmationBottomSheet(private val message : String,private val yesFunction: () -> Unit) : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: ConfirmationBottomSheetLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConfirmationBottomSheetLayoutBinding.inflate(inflater)
        binding.buttonYes.setOnClickListener(this)
        binding.buttonNo.setOnClickListener(this)
        binding.message.text = message
        return binding.root
    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.button_yes -> {
                yesFunction()
                this.dismiss()
            }
            R.id.button_no -> {
                this.dismiss()
            }
        }
    }

    companion object {
        val TAG = ConfirmationBottomSheet::javaClass.name
    }
}