package iooojik.anon.meet.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.security.ProviderInstaller
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.ActivityMainBinding
import iooojik.anon.meet.net.rest.RetrofitHelper

class MainActivity : AppCompatActivity(), ActivityMainLogic {
    companion object{
        var themeChanged = false
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        RetrofitHelper.doRetrofit()
        if (!themeChanged) {
            changeTheme(this, false)
            themeChanged = !themeChanged
        }
        else {
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
        }
    }



    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()

}