package iooojik.anon.meet.data.models

import com.google.gson.annotations.SerializedName
import iooojik.anon.meet.md5

class User(
    @SerializedName("uuid")
    val uuid: String = mUuid,
    @SerializedName("userLogin")
    val userLogin: String = mUserLogin,
    @SerializedName("lastLogin")
    val lastLogin: String = mLastLogin,
    @SerializedName("birthDate")
    val birthDate: String = mBirthDate,
    @SerializedName("password")
    val password: String = mPassword,
    @SerializedName("filter")
    val filter: Filter = mFilter,
    @SerializedName("positiveReputation")
    val positiveReputation: Int? = mPositiveReputation,
    @SerializedName("negativeReputation")
    val negativeReputation: Int? = mNegativeReputation
) {
    companion object {
        @JvmStatic
        var mUuid: String = ""

        @JvmStatic
        var mUserLogin: String = ""

        @JvmStatic
        var mLastLogin: String = ""

        @JvmStatic
        var mBirthDate: String = ""

        @JvmStatic
        var mPassword: String = ""
            set(value) {
                field = md5(value)
            }

        @JvmStatic
        var mFilter: Filter = Filter()

        @JvmStatic
        var mPositiveReputation: Int? = 0

        @JvmStatic
        var mNegativeReputation: Int? = 0
    }
}