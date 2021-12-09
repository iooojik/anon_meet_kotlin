package iooojik.anon.meet.ui.auth.registration

import android.app.Activity
import android.view.View
import androidx.navigation.NavController
import iooojik.anon.meet.data.models.User
import iooojik.anon.meet.databinding.FragmentRegistration2Binding
import iooojik.anon.meet.net.rest.RetrofitHelper
import iooojik.anon.meet.ui.auth.login.LoginFragmentLogic
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface RegistrationFragment2Logic : View.OnClickListener, LoginFragmentLogic {
    fun setListeners(binding: FragmentRegistration2Binding) {
        binding.registerButton.setOnClickListener(this)
        binding.selectBirthDate.setOnClickListener(this)
    }

    fun register(
        binding: FragmentRegistration2Binding,
        navController: NavController,
        activity: Activity
    ) {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitHelper.authService.registration(User()).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                binding.progressBar.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    auth(navController, activity, null)
                } else RetrofitHelper.onUnsuccessfulResponse(
                    binding.root,
                    response.errorBody(),
                    activity
                )
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                RetrofitHelper.onFailure(t)
                binding.progressBar.visibility = View.INVISIBLE
            }

        })
    }

    fun formatDate(date: String): String {
        val dateArr = date.split('.')
        return "${dateArr[0].toInt()}.${dateArr[1].toInt()}.${dateArr[2].toInt()}"
    }
}