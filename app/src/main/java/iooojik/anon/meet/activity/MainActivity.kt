package iooojik.anon.meet.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
        setUpToolBar(binding, findNavController(R.id.nav_host_fragment), this)
        setToolBarMenuClickListener(binding, resources, theme, findNavController(R.id.nav_host_fragment))
        checkUserTokenAndAuth(activity = this, findNavController(R.id.nav_host_fragment))
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()

}