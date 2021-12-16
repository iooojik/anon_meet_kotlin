package iooojik.anon.meet.activity.tests

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import iooojik.anon.meet.ActivityRule
import iooojik.anon.meet.R
import iooojik.anon.meet.activity.ActivityMainLogic
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityMainTest : ActivityMainLogic {
    @Rule
    @JvmField
    val activityRule = ActivityRule()

    @Test
    fun checkUserTokenAndAuthTest() {
        activityRule.activityTestRule.runOnUiThread {
            checkUserTokenAndAuth(
                ApplicationProvider.getApplicationContext(),
                activityRule.navController
            )
        }
        val prefs = SharedPreferencesManager(ApplicationProvider.getApplicationContext())
        prefs.initPreferences()
        val token = prefs.getValue(
            SharedPrefsKeys.USER_TOKEN,
            ""
        )
        if (token is String? && !token.isNullOrBlank())
            assert(
                activityRule.navController.currentDestination!!.id == R.id.filtersFragment
                        || activityRule.navController.currentDestination!!.id == R.id.chatProcessFragment
            )
        else
            assert(activityRule.navController.currentDestination!!.id == R.id.loginFragment)
    }
}