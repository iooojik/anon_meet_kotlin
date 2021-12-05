package iooojik.anon.meet.models

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel


class UserViewModel : ViewModel() {
    companion object {
        var userMutableLiveData = MutableLiveData<User>()

        fun changeUserInfo(newUserInfo: User) {
            User.mUuid = newUserInfo.uuid
            User.mBirthDate = newUserInfo.birthDate
            User.mFilter = newUserInfo.filter
            User.mLastLogin = newUserInfo.lastLogin
            User.mUserLogin = newUserInfo.userLogin
            userMutableLiveData.value = User()
        }

        fun changeInterlocutorAges(interlocutorAges: String) {
            User.mFilter.interlocutorAges = interlocutorAges
            userMutableLiveData.value = User()
        }

        fun newModel() {
            userMutableLiveData.value = User()
        }

        init {
            userMutableLiveData.value = User()
        }
    }
}