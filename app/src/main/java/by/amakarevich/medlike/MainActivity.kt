package by.amakarevich.medlike

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {

    private lateinit var model: ViewModelFireBase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MyLog", "MainActitvity")
        initViewModel()
        initFragment()
    }

    private fun initViewModel() {
        model = ViewModelProviders.of(this).get(ViewModelFireBase::class.java)

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
        val fragmentDetailMedCenter =
            FragmentDetailMedCenter.newInstance(imageUrl, name, numberOfLikes, numberOfDislikes, rating)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragmentDetailMedCenter, "StartDetail")
            .addToBackStack("FragmentStack")
        fragmentTransaction.commit()
        Log.d("MyLog", "FragmentDetailStarted")
    }

    private fun initFragment() {
        title = resources.getString(R.string.app_name)
        val fragmentLIST = FragmentList.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, fragmentLIST, "Start")
        fragmentTransaction.commit()
        Log.d("MyLog", "startInitFragment")

    }


}