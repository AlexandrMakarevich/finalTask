package by.amakarevich.medlike

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.amakarevich.medlike.data.MedCenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ViewModelFireBase : ViewModel() {

    private val repository = RepositoryFireBase()
    private val _data = MutableLiveData<List<MedCenter>>()
    val data: LiveData<List<MedCenter>> get() = _data

    init {
        viewModelScope.launch {
            Log.d("MyLog", "ViewModelScope launch")
            repository.getListOfSnapshotMedCenters().collect {
                _data.value = it
            }
        }
    }

    fun getAllMedCenters(): Flow<List<MedCenter>> {
        return repository.getAllMedCenters()
    }

    suspend fun updateDataMedCentres(document: String, data: HashMap<String, Int>) {
        repository.updateDataMedCentres(document, data)
    }

    suspend fun updateDataUserMedCenterLike(
        userID: String,
        nameMedCenter: String,
        data: HashMap<String, String>
    ) {
        repository.updateDataUserMedCenterLike(userID, nameMedCenter, data)
    }

    suspend fun likeIs(name: String, userID: String): String {
        return repository.likeIs(name, userID)
    }

    suspend fun addMedCenterInUserMedCenterLike(
        user: String,
        nameMedCenter: String,
        data: Map<String, Any>
    ) {
        repository.addMedCenterInUserMedCenterLike(user, nameMedCenter, data)
    }

    //
    // if "currentMedCenter" was changed from FragmentList
    // call FragmentDETAILMEDCENTER from MainActivity
    val currentMedCenter: MutableLiveData<List<Any?>> by lazy {
        MutableLiveData<List<Any?>>()
    }

    val numberOfLikes: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val numberOfDislikes: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    override fun onCleared() {
        super.onCleared()
        isloadDetail = true
        Log.d("MyLog", "ViewModel_OnCleared")
    }

    companion object {
        var isloadDetail = true
    }
}