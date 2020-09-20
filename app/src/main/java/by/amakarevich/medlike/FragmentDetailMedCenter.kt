package by.amakarevich.medlike

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import coil.api.load
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentDetailMedCenter : Fragment() {
    private val myViewModel: ViewModelFireBase by activityViewModels()
    private val db = Firebase.firestore

    private val spref: SharedPreferences?
        get() {
            return activity?.getSharedPreferences("Preference", AppCompatActivity.MODE_PRIVATE)
        }
    private var like = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_medcenter, container, false)
        Log.d("MyLog", "OnCreateView FragmentDetailMedCenter")

        val imageLogo = view.findViewById<ImageView>(R.id.image_logo_detail)
        val textViewName = view.findViewById<TextView>(R.id.name_detail)
        val numberOfLikes = view.findViewById<TextView>(R.id.numberOfLikes)
        val numberOfDislikes = view.findViewById<TextView>(R.id.numberOfDislikes)
        val buttonThumbUp = view.findViewById<ImageButton>(R.id.thumb_up)
        val buttonThumbDown = view.findViewById<ImageButton>(R.id.thumb_down)

        // TODO: 15.09.2020 to set buttons visibility

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            like = myViewModel.likeIs(
                arguments?.getString(NAME).toString(),
                spref?.getString(EnumSharedPreferences.UserID.toString(), "").toString()
            )
            when (like) {
                EnumLike.LIKE.toString() -> {
                    buttonThumbUp.isEnabled = false
                    buttonThumbDown.isEnabled = true
                }
                EnumLike.DISLIKE.toString() -> {
                    buttonThumbUp.isEnabled = true
                    buttonThumbDown.isEnabled = false
                }
                EnumLike.ZERO.toString() -> {
                    buttonThumbUp.isEnabled = true
                    buttonThumbDown.isEnabled = true
                }
                "" -> {
                    // add medcenter in userMedCenterLike
                    val nameMedCenter = arguments?.getString(NAME).toString()
                    val data = mapOf<String, Any>(
                        "like" to EnumLike.ZERO,
                        "nameMedcenter" to nameMedCenter
                    )
                    val user =
                        spref?.getString(EnumSharedPreferences.UserID.toString(), "").toString()
                    val scope = CoroutineScope(Dispatchers.Main)
                    scope.launch {
                        myViewModel.addMedCenterInUserMedCenterLike(user, nameMedCenter, data)
                    }
                }
                else -> {
                    // TODO: 18.09.2020 Something wrong
                }
            }
            Log.d("MyLog", "LIIIIIIKE=============== $like")
        }


        myViewModel.numberOfLikes.value = arguments?.getInt(NUMBEROFLIKES)
        myViewModel.numberOfDislikes.value = arguments?.getInt(NUMBEROFDISLIKES)

        myViewModel.numberOfLikes.observe(viewLifecycleOwner, Observer {
            numberOfLikes.text =
                resources.getString(R.string.numberOfLike) + it.toString()
        })

        myViewModel.numberOfDislikes.observe(viewLifecycleOwner, Observer {
            numberOfDislikes.text =
                resources.getString(R.string.numberOfDislike) + it.toString()
        })

        imageLogo.load(arguments?.getString(IMAGE_URL))
        textViewName.text = arguments?.getString(NAME)
        numberOfLikes.text =
            resources.getString(R.string.numberOfLike) + arguments?.getInt(NUMBEROFLIKES)
        numberOfDislikes.text =
            resources.getString(R.string.numberOfDislike) + arguments?.getInt(NUMBEROFDISLIKES)

        val onClickListener = View.OnClickListener {
            when (it) {
                buttonThumbUp -> {
                    Log.d("MyLog", "Pressed thumb_up")
                    buttonThumbUp.isEnabled = false
                    buttonThumbDown.isEnabled = true
                    updateFireBaseRatingPlusOne()
                }
                buttonThumbDown -> {
                    Log.d("MyLog", "Pressed thumb_down")
                    buttonThumbUp.isEnabled = true
                    buttonThumbDown.isEnabled = false
                    updateFireBaseRatingMinusOne()
                }
            }
        }
        buttonThumbUp.setOnClickListener(onClickListener)
        buttonThumbDown.setOnClickListener(onClickListener)

        return view
    }

    private fun updateFireBaseRatingPlusOne() {
        var numberOfLikes = arguments?.getInt(NUMBEROFLIKES)!!
        var numberOfDislikes: Int = arguments?.getInt(NUMBEROFDISLIKES)!!

        when (like){
            EnumLike.LIKE.toString() -> {
                Log.d("MyLog", "It is impossible")
            }
            EnumLike.DISLIKE.toString() -> {
                numberOfLikes += 1
                numberOfDislikes -= 1
                arguments?.apply {
                    putInt(NUMBEROFLIKES, numberOfLikes)
                    putInt(NUMBEROFDISLIKES, numberOfDislikes)
                }
                myViewModel.numberOfLikes.value = numberOfLikes
                myViewModel.numberOfDislikes.value = numberOfDislikes
            }
            EnumLike.ZERO.toString(), "" -> {
                numberOfLikes += 1
                arguments?.putInt(NUMBEROFLIKES, numberOfLikes)
                myViewModel.numberOfLikes.value = numberOfLikes
            }
        }
        like = EnumLike.LIKE.toString()
        val data = hashMapOf(
            "rating" to rating(numberOfLikes, numberOfDislikes),
            "numberOfLikes" to numberOfLikes
        )
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            myViewModel.updateDataMedCentres(arguments?.getString(NAME).toString(), data)
        }

        val data1 = hashMapOf(
            "like" to EnumLike.LIKE.toString()
        )
        val scope1 = CoroutineScope(Dispatchers.Main)
        scope1.launch {
            myViewModel.updateDataUserMedCenterLike(
                spref?.getString(EnumSharedPreferences.UserID.toString(), "").toString(),
                arguments?.getString(NAME).toString(),
                data1
            )
        }
    }

    private fun updateFireBaseRatingMinusOne() {
        var numberOfLikes = arguments?.getInt(NUMBEROFLIKES)!!
        var numberOfDislikes: Int = arguments?.getInt(NUMBEROFDISLIKES)!!

        when (like){
            EnumLike.LIKE.toString() -> {
                numberOfDislikes += 1
                numberOfLikes -= 1
                arguments?.apply {
                    putInt(NUMBEROFLIKES, numberOfLikes)
                    putInt(NUMBEROFDISLIKES, numberOfDislikes)
                }
                myViewModel.numberOfLikes.value = numberOfLikes
                myViewModel.numberOfDislikes.value = numberOfDislikes
            }
            EnumLike.DISLIKE.toString() -> {
                Log.d("MyLog", "It is impossible")
            }
            EnumLike.ZERO.toString(), "" -> {
                numberOfDislikes += 1
                arguments?.putInt(NUMBEROFLIKES, numberOfDislikes)
                myViewModel.numberOfDislikes.value = numberOfDislikes
            }
        }
        like = EnumLike.DISLIKE.toString()
        val data = hashMapOf(
            "rating" to rating(numberOfLikes, numberOfDislikes),
            "numberOfDislikes" to numberOfDislikes
        )
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            myViewModel.updateDataMedCentres(arguments?.getString(NAME).toString(), data)
        }

        val data1 = hashMapOf(
            "like" to EnumLike.DISLIKE.toString()
        )
        val scope1 = CoroutineScope(Dispatchers.Main)
        scope1.launch {
            myViewModel.updateDataUserMedCenterLike(
                spref?.getString(EnumSharedPreferences.UserID.toString(), "").toString(),
                arguments?.getString(NAME).toString(),
                data1
            )
        }
    }

    private fun rating(like: Int, dislike: Int): Int {
        val rating: Float = if (like + dislike != 0) {
            ((like - dislike).toFloat() / (like + dislike)) * 100
        } else 0f

        return rating.toInt()
    }


    companion object {
        @JvmStatic
        fun newInstance(
            imageUrl: String,
            name: String,
            numberOfLikes: Int,
            numberOfDislikes: Int,
            rating: Int
        ) =
            FragmentDetailMedCenter().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_URL, imageUrl)
                    putString(NAME, name)
                    putInt(NUMBEROFLIKES, numberOfLikes)
                    putInt(NUMBEROFDISLIKES, numberOfDislikes)
                    putInt(RATING, rating)
                }
            }
        private const val IMAGE_URL = "ImageUrl"
        private const val NAME = "Name"
        private const val NUMBEROFLIKES = "NumberOfLikes"
        private const val NUMBEROFDISLIKES = "numberOfDislikes"
        private const val RATING = "Rating"

    }

}


