package iooojik.anon.meet.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.security.ProviderInstaller
import iooojik.anon.meet.AdUtil
import iooojik.anon.meet.AppDatabase
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.ActivityMainBinding
import iooojik.anon.meet.net.rest.RetrofitHelper

class MainActivity : AppCompatActivity(), ActivityMainLogic {

    lateinit var binding: ActivityMainBinding
    private val fragmentsWithNoAd = listOf(R.id.chatProcessFragment, R.id.aboutAppFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        RetrofitHelper.doRetrofit()
        setUpToolBar(binding, findNavController(R.id.nav_host_fragment), this)
        setToolBarMenuClickListener(
            binding,
            resources,
            theme,
            findNavController(R.id.nav_host_fragment),
            this
        )
        binding.appBarMain.include.adBanner.loadAd(AdRequest.Builder().build())
        checkUserTokenAndAuth(
            activity = this,
            navController = findNavController(R.id.nav_host_fragment)
        )
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, destination, _ ->
            fragmentsWithNoAd.forEach {
                if (destination.id == it)
                    binding.appBarMain.include.adBanner.visibility = View.GONE
            }
        }
        ProviderInstaller.installIfNeeded(applicationContext)
        AppDatabase.initDatabase(this)
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()

}