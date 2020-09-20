package by.amakarevich.medlike

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RepositoryFireBase() {

    private val db = Firebase.firestore

    suspend fun getDataBase(): QuerySnapshot? {
        return FireBaseAccess.getListOfMedCenters()
    }

    //========================================================
    suspend fun updateDataMedCentres(document: String, data: HashMap<String, Int>) {
        db
            .collection("medcenters")
            .document(document)
            .set(data, SetOptions.merge())
            .await()
    }
//=========================================================

    suspend fun updateDataUserMedCenterLike(
        userID: String,
        nameMedCenter: String,
        data: HashMap<String, String>
    ) {
        Log.d(
            "MyLog",
            "FROM updateDataUserMedCenterLike=============== $userID  ======  $nameMedCenter ==== ${data["like"]}"
        )
        db
            .collection("userMedCenterLike")
            .document(userID)
            .collection("medcenter")
            .document(nameMedCenter)
            .set(data, SetOptions.merge())
            .await()
    }

    suspend fun likeIs(name: String, userID: String): String {
        return try {
            var like: String = ""
            Log.d("MyLog", "FROM LIKEIS=============== $name  ======  $userID")
            db
                .collection("userMedCenterLike")
                .document(userID)
                .collection("medcenter")
                .whereEqualTo("nameMedcenter", name)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        like = doc.data["like"].toString()
                        Log.d("MyLog", "LIIIIIIKE FROM LIKEIS=============== $like")
                    }
                }
                .await()
            like
        } catch (e: Exception) {
            "exeption $e"
        }
    }

    suspend fun addMedCenterInUserMedCenterLike(
        user: String,
        nameMedCenter: String,
        data: Map<String, Any>
    ) {
        db
            .collection("userMedCenterLike").document(user)
            .collection("medcenter").document(nameMedCenter)
            .set(data)
            .await()
    }
}