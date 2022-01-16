package iooojik.anon.meet.ui.about.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import iooojik.anon.meet.BuildConfig
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentAboutAppBinding
import iooojik.anon.meet.openURL
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class AboutAppFragment : Fragment() {
    private lateinit var binding: FragmentAboutAppBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutAppBinding.inflate(inflater)
        binding.version = "${BuildConfig.VERSION_NAME}/${BuildConfig.VERSION_CODE}"
        Picasso.get().load(R.mipmap.ic_launcher_foreground)
            .transform(CropCircleTransformation()).error(R.drawable.ic_launcher_foreground)
            .into(binding.logo)
        binding.fragment = this
        return binding.root
    }

    fun goToVK(view: View?) {
        view?.let {
            openURL("https://vk.com/iooojik_blog", requireContext())
        }
    }

    fun goToTG(view: View?) {
        view?.let {
            openURL("https://t.me/iooojik", requireContext())
        }
    }

    fun goToTerms(view: View?) {
        view?.let {
            openURL("https://iooojik.ru/anon.api/terms", requireContext())
        }
    }

    fun goToPolicy(view: View?) {
        view?.let {
            openURL("https://iooojik.ru/anon.api/policy", requireContext())
        }
    }

    fun goToMarkApp(view: View?) {
        view?.let {
            openURL(
                "https://play.google.com/store/apps/details?id=iooojik.anon.meet",
                requireContext()
            )
        }
    }


}