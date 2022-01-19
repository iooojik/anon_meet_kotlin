package iooojik.anon.meet.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.security.ProviderInstaller
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import iooojik.anon.meet.AppDatabase
import iooojik.anon.meet.Application
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.databinding.ActivityMainBinding
import iooojik.anon.meet.log
import iooojik.anon.meet.net.ConnectionType
import iooojik.anon.meet.net.NetworkMonitorUtil
import iooojik.anon.meet.net.rest.RetrofitHelper

class MainActivity : AppCompatActivity(), ActivityMainLogic {

    lateinit var binding: ActivityMainBinding
    private val networkMonitor = NetworkMonitorUtil(this)
    private lateinit var noConnectionMessage : AlertDialog

    init {
        val context = Application.instance().applicationContext
        RetrofitHelper.doRetrofit()
        ProviderInstaller.installIfNeeded(context)
        AppDatabase.initDatabase(context)
        loadInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUI()
        setNetworkMonitorListener()
    }

    private fun setUI() {
        getStartDestination(this)
        setUpToolBar(navController = findNavController(R.id.nav_host_fragment), activity = this)
        setToolBarMenuClickListener(navController = findNavController(R.id.nav_host_fragment), activity = this)
        hideAdOnFragmentsListener()
        noConnectionMessage = MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.error))
            .setMessage(resources.getString(R.string.no_internet_connection))
            .setCancelable(false).create()
        binding.appBarMain.include.adBanner.loadAd(AdRequest.Builder().build())
    }

    private fun loadInfo() {
        checkUserTokenAndAuth(activity = this)
    }

    private fun hideAdOnFragmentsListener(){
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, destination, _ ->
            listOf(R.id.chatProcessFragment, R.id.aboutAppFragment).forEach {
                if (destination.id == it)
                    binding.appBarMain.include.adBanner.visibility = View.GONE
            }
        }
    }

    private fun setNetworkMonitorListener() {
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                log("NETWORK_MONITOR_STATUS Wifi Connection")
                            }
                            ConnectionType.Cellular -> {
                                log("NETWORK_MONITOR_STATUS Cellular Connection")
                            }
                            else -> {}
                        }
                        if (noConnectionMessage.isShowing)
                            noConnectionMessage.hide()
                        if (User.mUuid.isBlank())
                            loadInfo()
                    }
                    false -> {
                        log("NETWORK_MONITOR_STATUS No Connection")
                        if (!noConnectionMessage.isShowing)
                            noConnectionMessage.show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()

}