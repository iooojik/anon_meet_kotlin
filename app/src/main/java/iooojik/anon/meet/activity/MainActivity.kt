package iooojik.anon.meet.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.security.ProviderInstaller
import iooojik.anon.meet.AppDatabase
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.ActivityMainBinding
import iooojik.anon.meet.net.rest.RetrofitHelper

class MainActivity : AppCompatActivity(), ActivityMainLogic {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        RetrofitHelper.doRetrofit()
        //Application.getInstance().
        //changeTheme(this, false)
        setUpToolBar(binding, findNavController(R.id.nav_host_fragment), this)
        setToolBarMenuClickListener(
            binding,
            resources,
            theme,
            findNavController(R.id.nav_host_fragment),
            this
        )
        checkUserTokenAndAuth(context = this, findNavController(R.id.nav_host_fragment))
        ProviderInstaller.installIfNeeded(applicationContext)
        AppDatabase.initDatabase(this)
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()

}