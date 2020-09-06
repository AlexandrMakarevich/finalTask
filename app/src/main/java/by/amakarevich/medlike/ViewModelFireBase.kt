package by.amakarevich.medlike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch


class ViewModelFireBase : ViewModel() {


    private val _data = MutableLiveData<QuerySnapshot>()
    val data: LiveData<QuerySnapshot> get() = _data
    init {
        viewModelScope.launch {
            _data.value = FireBaseAccess.getListOfMedCenters()
        }
    }


    //
    // if "currentMedCenter" was changed from FragmentList
    // call FragmentDETAILMEDCENTER from MainActivity
    val currentMedCenter: MutableLiveData<MutableList<Any?>> by lazy {
        MutableLiveData<MutableList<Any?>>()
    }

    val numberOfLikes: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val numberOfDislikes: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

}