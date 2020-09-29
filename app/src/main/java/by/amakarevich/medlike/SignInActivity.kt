package by.amakarevich.medlike

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private val myViewModel: ViewModelFireBase by viewModels()
    private var mSavedInstanceState: Bundle? = null

    private val spref: SharedPreferences
        get() {
            return getSharedPreferences("Preference", MODE_PRIVATE)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MyLog", "SignActivity_OnCreate")

        mSavedInstanceState = savedInstanceState

        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser?.uid
                spref.edit().putString(EnumSharedPreferences.UserID.toString(), user.toString())
                    .apply()
                Log.d("MyLog", "RESULT_OK")
                val list = mutableListOf(
                    mSavedInstanceState?.getString("ImageUrl").toString(),
                    mSavedInstanceState?.getString("Name").toString(),
                    mSavedInstanceState?.getInt("NumberOfLikes"),
                    mSavedInstanceState?.getInt("NumberOfDislikes"),
                    mSavedInstanceState?.getInt("Rating")
                )
                myViewModel.currentMedCenter.value = list
                finish()
            } else {
                Log.d("MyLog", "RESULT_FAILED")
                finish()
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}