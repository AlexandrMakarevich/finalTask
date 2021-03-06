package by.amakarevich.medlike

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
        Log.d("MyLog", "MainActitvity_OnCreate")
        initSharedPreference()
        initViewModel()
        initFragment()
        if (savedInstanceState == null) {
            spref.edit().putString(themeMode, EnumThemeMode.DARK.toString()).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyLog", "MainActitvity_OnDestroy")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        mMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.signOut)?.isVisible = mFirebaseUser != null
        menu?.findItem(R.id.dark_bright)?.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
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
            R.id.dark_bright -> {
                ViewModelFireBase.isloadDetail = false
                val currentThemeMode = spref.getString(themeMode, "")
                Log.d("MyLog", "MainActivity_currentThemeMode == $currentThemeMode")
                when (currentThemeMode) {
                    EnumThemeMode.BRIGHT.toString() -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        spref.edit().putString(themeMode, EnumThemeMode.DARK.toString()).apply()
                    }
                    EnumThemeMode.DARK.toString() -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        spref.edit().putString(themeMode, EnumThemeMode.BRIGHT.toString()).apply()
                    }
                }
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
        val currentMedCenter = Observer<List<Any?>> { currentMedCenter ->
            showFragmentDETAILMEDCENTER(
                currentMedCenter[0] as String, // image
                currentMedCenter[1] as String, // name
                currentMedCenter[2] as Int, // numberOfLikes
                currentMedCenter[3] as Int, // numberOfDislikes
                currentMedCenter[4] as Int // rating
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
        mMenu?.findItem(R.id.dark_bright)?.isVisible = false

        if (ViewModelFireBase.isloadDetail) {
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
    }

    private fun initFragment() {
        title = resources.getString(R.string.app_name)
        if (ViewModelFireBase.isloadDetail) {
            val fragmentLIST = FragmentList.newInstance()
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.container, fragmentLIST, "FRAGMENT_LIST")
            fragmentTransaction.commit()
            Log.d("MyLog", "startInitFragment")
        }
        ViewModelFireBase.isloadDetail = false
    }

    private fun initSharedPreference() {
        if (!spref.contains(EnumSharedPreferences.UserID.toString())) {
            spref.edit().putString(EnumSharedPreferences.UserID.toString(), "Anonimus").apply()
        }
        if (!spref.contains(themeMode)) {
            spref.edit().putString(themeMode, EnumThemeMode.DARK.toString()).apply()
        }
    }

    companion object {
        const val themeMode: String = "THEME_MODE"
    }
}