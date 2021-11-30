package iooojik.anon.meet.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ActivityMainLogic {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolBar(binding, findNavController(R.id.nav_host_fragment))
        setToolBarMenuClickListener(binding, resources, theme)
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()

}