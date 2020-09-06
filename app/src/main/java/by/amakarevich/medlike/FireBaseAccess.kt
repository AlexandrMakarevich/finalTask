package by.amakarevich.medlike

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FireBaseAccess {

    private val myFirestore = Firebase.firestore

    suspend fun getListOfMedCenters(): QuerySnapshot?{
        Log.d("MyLog", "LOAD DATA FROM FIREBASE")
        return try{
            val data = myFirestore
                .collection("medcenters")
                .orderBy("rating", Query.Direction.DESCENDING )
                .get()
                .await()
            data
        } catch (e: Exception){
            null
        }
    }


}