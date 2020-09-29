package by.amakarevich.medlike

import android.util.Log
import by.amakarevich.medlike.data.MedCenter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

object FireBaseAccess {

    private val myFirestore = Firebase.firestore

    suspend fun getListOfSnapshotMedCenters(): Flow<List<MedCenter>> = flow {
        val list: MutableList<MedCenter> = mutableListOf()
        myFirestore
            .collection("medcenters")
            .orderBy("rating", Query.Direction.DESCENDING)
            .addSnapshotListener { value, _ ->
                list.clear()
                for (doc in value!!) {
                    Log.d(
                        "MyLog",
                        "FireBaseAccess(getListOfMedCenters_addSnapshotListener): doc = $doc"
                    )
                    val medCenter = doc.toObject(MedCenter::class.java)
                    list.add(medCenter)
                }
            }
        emit(list)
    }

    suspend fun getListOfMedCenters(): List<MedCenter> {
        Log.d("MyLog", "LOAD DATA FROM FIREBASE")
        return try {
            val list: MutableList<MedCenter> = mutableListOf()
            myFirestore
                .collection("medcenters")
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        Log.d(
                            "MyLog",
                            "FireBaseAccess(addOnSuccessListener):document = $document"
                        )
                        val medCenter = document.toObject(MedCenter::class.java)
                        list.add(medCenter)
                    }
                }
                .await()
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}