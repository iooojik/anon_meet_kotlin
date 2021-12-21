package iooojik.anon.meet

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.rule.ActivityTestRule
import iooojik.anon.meet.activity.MainActivity
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.MockitoAnnotations

class ActivityRule(private val startDestination: Int? = null) : TestRule {

    lateinit var navController: NavController

    lateinit var activityTestRule: ActivityTestRule<MainActivity>

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                before()
                try {
                    base?.evaluate()
                } catch (e: Exception) {
                    throw e
                }
                after()
            }
        }
    }

    fun before() {
        MockitoAnnotations.openMocks(this)
        activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)
        activityTestRule.launchActivity(Intent())
        activityTestRule.runOnUiThread {
            navController = activityTestRule.activity.findNavController(R.id.nav_host_fragment)
            if (startDestination != null) {
                val currentFragment = navController.currentDestination!!.id
                if (startDestination == R.id.filtersFragment && currentFragment == R.id.loginFragment)
                    navController.navigate(R.id.action_global_filtersFragment)
                else if (startDestination == R.id.loginFragment && currentFragment == R.id.filtersFragment)
                    navController.navigate(R.id.action_global_auth_navigation)
                else
                    navController.navigate(startDestination)
            }
            /*navController = TestNavHostController(activityTestRule.activity)
            navController.setGraph(R.navigation.app_navigation)
            Navigation.setViewNavController(
                activityTestRule.activity.requireViewById(R.id.nav_host_fragment),
                navController
            )*/
        }
    }

    fun after() {

    }
}