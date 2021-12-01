package iooojik.anon.meet.models

import com.google.gson.annotations.SerializedName

data class Filter(

    @SerializedName("myAge")
	val myAge: Int = 18,
    @SerializedName("interlocutorAges")
	val interlocutorAges: String = "18/18",
    @SerializedName("interlocutorSex")
	val interlocutorSex: String = "male",
    @SerializedName("mySex")
	val mySex: String = "male"
)