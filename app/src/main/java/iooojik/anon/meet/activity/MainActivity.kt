package iooojik.anon.meet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    private val activityMainLogic = ActivityMainLogic()
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityMainLogic.setUpToolBar(binding, findNavController(R.id.nav_host_fragment))
        activityMainLogic.setToolBarMenuClickListener(binding, resources, theme)

    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nav_host_fragment).navigateUp()


}