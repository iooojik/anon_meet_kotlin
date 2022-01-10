package iooojik.anon.meet.data.models.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.annotations.SerializedName

data class SearchStateModel(
    @SerializedName("inSearchUsers")
    var inSearchUsers: Int = 0
)

class SearchStateViewModel(
    val data: MutableLiveData<SearchStateModel> = MutableLiveData(
        SearchStateModel(0)
    )
) : ViewModel()