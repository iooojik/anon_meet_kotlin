package iooojik.anon.meet

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import iooojik.anon.meet.activity.MainActivity
import iooojik.anon.meet.data.models.User
import iooojik.anon.meet.net.rest.RetrofitHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    private lateinit var navController: NavController
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Before
    fun beforeMethod(){
        MockitoAnnotations.openMocks(this)
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        activityScenario.onActivity {
            navController.setGraph(R.navigation.app_navigation)
            Navigation.setViewNavController(it.requireViewById(R.id.nav_host_fragment), navController)
            navController.navigate(R.id.loginFragment)
        }
    }

    @Test
    fun registerButtonTest() {
        //assert(navController.currentDestination?.id == R.id.loginFragment)

        onView(withId(R.id.go_to_registration_button)).perform(click())
        assert(navController.currentDestination?.id == R.id.registrationFragment1)


        //onView(withId(R.id.go_to_registration_button)).perform(click())
//        assert(navController.currentDestination?.id == R.id.registrationFragment1)
        /*val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        // Create a graphical FragmentScenario for the TitleScreen
        val titleScenario = launchFragmentInContainer<LoginFragment>()

        titleScenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.app_navigation)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // Verify that performing a click changes the NavController’s state
        navController.navigate(R.id.loginFragment)
        */
        //val scenario = launchFragmentInContainer<LoginFragment>()
        //onView(withId(R.id.search_button)).perform(click())
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