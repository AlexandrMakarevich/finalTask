package by.amakarevich.medlike

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private val mFirebaseAuth: FirebaseAuth
        get() {
            return FirebaseAuth.getInstance()
        }
    private val mFirebaseUser: FirebaseUser?
        get() {
            return mFirebaseAuth.currentUser
        }

    private val model: ViewModelFireBase by viewModels()

    private val constraintlayout: ConstraintLayout
        get() {
            return findViewById(R.id.mainLayout)
        }
    private var mMenu: Menu? = null

    private val spref: SharedPreferences
        get() {
            return getSharedPreferences("Preference", MODE_PRIVATE)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MyLog", "MainActitvity")
        initViewModel()
        initFragment()
        initSharedPreference()
    }


    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        mMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.signOut)?.isVisible = mFirebaseUser != null
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOut -> {
                Log.d("MyLog", "button signOut is pressed")
                val snackbar: Snackbar = Snackbar.make(
                    constraintlayout,
                    "Вы вышли из учетной записи",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
                mFirebaseAuth.signOut()
                invalidateOptionsMenu()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag("FRAGMENT_DetailMedCenter")
        if (fragment != null && fragment.isVisible) {
            title = resources.getString(R.string.app_name)
            invalidateOptionsMenu()
        }
        super.onBackPressed()
    }

    private fun initViewModel() {
        val currentMedCenter = Observer<MutableList<Any?>> { currentMedCenter ->
            showFragmentDETAILMEDCENTER(
                currentMedCenter[0] as String, // image
                currentMedCenter[1] as String, // name
                currentMedCenter[2] as Int, // numberOfLikes
                currentMedCenter[3] as Int,  // numberOfDislikes
                currentMedCenter[4] as Int  // rating
            )
        }
        model.currentMedCenter.observe(this, currentMedCenter)
    }

    private fun showFragmentDETAILMEDCENTER(
        imageUrl: String,
        name: String,
        numberOfLikes: Int,
        numberOfDislikes: Int,
        rating: Int
    ) {
        title = resources.getString(R.string.setLikeOrDislike)
        mMenu?.findItem(R.id.signOut)?.isVisible = false
        val fragmentDetailMedCenter =
            FragmentDetailMedCenter.newInstance(
                imageUrl,
                name,
                numberOfLikes,
                numberOfDislikes,
                rating
            )
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.container,
            fragmentDetailMedCenter,
            "FRAGMENT_DetailMedCenter"
        )
            .addToBackStack("FragmentStack")
        fragmentTransaction.commit()
        Log.d("MyLog", "FragmentDetailStarted")
    }


    private fun initFragment() {
        title = resources.getString(R.string.app_name)
        val fragmentLIST = FragmentList.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, fragmentLIST, "FRAGMENT_LIST")
        fragmentTransaction.commit()
        Log.d("MyLog", "startInitFragment")

    }

    private fun initSharedPreference() {
        if (!spref.contains(EnumSharedPreferences.UserID.toString())) {
            spref.edit().putString(EnumSharedPreferences.UserID.toString(), "Anonimus").apply()
        }
    }

}