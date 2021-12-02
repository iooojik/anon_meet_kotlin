package iooojik.anon.meet.models

import com.google.gson.annotations.SerializedName

class Filter(

    @SerializedName("myAge")
    val myAge: Int = mMyAge,
    @SerializedName("interlocutorAges")
    var interlocutorAges: String = mInterlocutorAges,
    @SerializedName("interlocutorSex")
    val interlocutorSex: String = mInterlocutorSex,
    @SerializedName("mySex")
    val mySex: String = mMySex,


    ) {
    companion object {
        @JvmStatic
        var mMyAge = 18

        @SerializedName("interlocutorAges")
        var mInterlocutorAges: String = "18/18"

        @SerializedName("interlocutorSex")
        val mInterlocutorSex: String = "male"

        @SerializedName("mySex")
        val mMySex: String = "male"
    }

    val interlocutorAgesRange: ArrayList<Float>
        get() {
            return arrayListOf(
                this.interlocutorAges.split("/")[0].toFloat(),
                this.interlocutorAges.split("/")[1].toFloat()
            )
        }
}