package iooojik.anon.meet.data.models.user


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class UserViewModelProvider(viewModelStoreOwner: ViewModelStoreOwner, user: User = User()) :
    ViewModelProvider(viewModelStoreOwner) {

    var liveData: MutableLiveData<User> = get(UserViewModel::class.java).userLiveData


    init {
        liveData.value = user
    }

}