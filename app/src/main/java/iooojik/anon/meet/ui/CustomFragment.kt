package iooojik.anon.meet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class CustomFragment : Fragment() {
    abstract fun setUI()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUI()
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}