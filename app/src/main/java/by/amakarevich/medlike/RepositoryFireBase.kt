package by.amakarevich.medlike

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RepositoryFireBase {

    private val db = Firebase.firestore

    suspend fun getDataBase(): QuerySnapshot? {
        return FireBaseAccess.getListOfMedCenters()
    }
//========================================================
    suspend fun updateData(document: String, data: HashMap<String, Int>) {
    db
        .collection("medcenters")
        .document(document)
        .set(data, SetOptions.merge())
        .await()
    }
}