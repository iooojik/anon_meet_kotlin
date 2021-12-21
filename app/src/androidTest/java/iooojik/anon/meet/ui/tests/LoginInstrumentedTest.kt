package iooojik.anon.meet.ui.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputLayout
import iooojik.anon.meet.ActivityRule
import iooojik.anon.meet.R
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {
    @Rule
    @JvmField
    val activityRule = ActivityRule(R.id.loginFragment)
    private lateinit var prefsManager: SharedPreferencesManager


    @Before
    fun beforeMethod(){
        prefsManager = SharedPreferencesManager(activityRule.activityTestRule.activity.applicationContext)
        prefsManager.initPreferences()
        prefsManager.clearAll()
    }

    @Test
    fun registerButtonTest() {
        onView(withId(R.id.go_to_registration_button)).perform(ViewActions.click())
        assert(activityRule.navController.currentDestination!!.id == R.id.registrationFragment1)
    }

    @Test
    fun authTestWithUI(){
        onView(withId(R.id.nickname_text_field)).check { view, exception ->
            if (view is TextInputLayout) {
                view.editText!!.setText("iooojik")
            } else throw exception
        }
        onView(withId(R.id.password_text_field)).check { view, exception ->
            if (view is TextInputLayout) {
                view.editText!!.setText("123456")
            } else throw exception
        }
        runBlocking {
            launch(this.coroutineContext) {
                onView(withId(R.id.login_Button)).perform(ViewActions.click())
            }.join()
            delay(10000L)
            prefsManager.clearAll()
            assert(activityRule.navController.currentDestination!!.id == R.id.filtersFragment)
        }
    }
}