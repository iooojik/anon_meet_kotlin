package iooojik.anon.meet.ui.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import iooojik.anon.meet.ActivityRule
import iooojik.anon.meet.R
import iooojik.anon.meet.data.models.User
import iooojik.anon.meet.md5
import iooojik.anon.meet.net.rest.RetrofitHelper
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {
    @Rule
    @JvmField
    val activityRule = ActivityRule()

    @Before
    fun beforeMethod(){
        activityRule.activityTestRule.activity.runOnUiThread {
            activityRule.navController.navigate(R.id.loginFragment)
        }
    }

    @Test
    fun registerButtonTest() {
        assert(activityRule.navController.currentDestination!!.id == R.id.loginFragment)
        onView(withId(R.id.go_to_registration_button)).perform(ViewActions.click())
        assert(activityRule.navController.currentDestination!!.id == R.id.registrationFragment1)
        /*val loginFragment = launchFragmentInContainer<LoginFragment>()
        loginFragment.onFragment{fragment ->
            Navigation.setViewNavController(fragment.requireView(), activityRule.navController)
            activityRule.navController.navigate(R.id.loginFragment)

            onView(withId(R.id.go_to_registration_button)).perform(click())
            assert(activityRule.navController.currentDestination!!.id == R.id.registrationFragment1)
        }*/

    }

    //@Test
    fun authTest() {
        //true creds iooojik:123456
        RetrofitHelper.doRetrofit()
        val response =
            RetrofitHelper.authService.login(User(userLogin = "iooojik", password = md5("1234156")))
                .execute()
        assert(response.isSuccessful)
    }
}