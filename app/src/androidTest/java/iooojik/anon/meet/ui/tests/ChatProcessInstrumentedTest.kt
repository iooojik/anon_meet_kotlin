package iooojik.anon.meet.ui.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import iooojik.anon.meet.ActivityRule
import iooojik.anon.meet.R
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatProcessInstrumentedTest {
    @Rule
    @JvmField
    val activityRule = ActivityRule(R.id.filtersFragment)
    private lateinit var prefsManager: SharedPreferencesManager

    @Before
    fun beforeMethod() {
        prefsManager =
            SharedPreferencesManager(activityRule.activityTestRule.activity.applicationContext)
        prefsManager.initPreferences()
    }

    @Test
    fun isNeededFragment() {
        val rUUID = prefsManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "") as String?
        if (rUUID.isNullOrBlank())
            assert(activityRule.navController.currentDestination!!.id == R.id.filtersFragment)
        else
            assert(activityRule.navController.currentDestination!!.id == R.id.chatProcessFragment)
    }

    @Test
    fun searchRoomButtonTest() {
        assert(activityRule.navController.currentDestination!!.id == R.id.filtersFragment)
        runBlocking {
            launch {
                onView(withId(R.id.search_button)).perform(click())
                delay(13000L)
                assert(activityRule.navController.currentDestination!!.id == R.id.filtersFragment)
            }.join()
        }

    }

}