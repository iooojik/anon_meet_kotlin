package iooojik.anon.meet.data.models.user

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel


class UserViewModel(
    var userLiveData: MutableLiveData<User> = MutableLiveData<User>()
) : ViewModel() {

    companion object {
        var currentUser = MutableLiveData<User>()

        init {
            currentUser.value = User()
        }

        fun changeCurrentUserInfo(newUserInfo: User) {
            User.mUuid = newUserInfo.uuid
            User.mBirthDate = newUserInfo.birthDate
            User.mFilter = newUserInfo.filter
            User.mLastLogin = newUserInfo.lastLogin
            User.mUserLogin = newUserInfo.userLogin
            currentUser.value = User()
        }

        fun changeCurrentInterlocutorAges(interlocutorAges: String) {
            User.mFilter.interlocutorAges = interlocutorAges
            currentUser.value = User()
        }

        fun newCurrentModel() {
            currentUser.value = User()
        }
    }

}