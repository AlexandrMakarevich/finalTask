package by.amakarevich.medlike

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import coil.api.load
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentDetailMedCenter : Fragment() {
    private val myViewModel: ViewModelFireBase by activityViewModels()
    private val db = Firebase.firestore

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
        val thumbUp = view.findViewById<ImageButton>(R.id.thumb_up)
        val thumbDown = view.findViewById<ImageButton>(R.id.thumb_down)

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
                thumbUp -> {
                    Log.d("MyLog", "Pressed thumb_up")
                    updateFireBaseRatingPlusOne()
                }
                thumbDown -> {
                    Log.d("MyLog", "Pressed thumb_down")
                    updateFireBaseRatingMinusOne()
                }
            }
        }
        thumbUp.setOnClickListener(onClickListener)
        thumbDown.setOnClickListener(onClickListener)

        return view
    }

    private fun updateFireBaseRatingPlusOne() {

        val numberOfLikes: Int = arguments?.getInt(NUMBEROFLIKES)!! + 1
        arguments?.putInt(NUMBEROFLIKES, numberOfLikes)
        myViewModel.numberOfLikes.value = numberOfLikes

        val numberOfDislikes: Int = arguments?.getInt(NUMBEROFDISLIKES)!!

        val data = hashMapOf(
            "rating" to rating(numberOfLikes, numberOfDislikes),
            "numberOfLikes" to numberOfLikes
        )
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            myViewModel.updateData(arguments?.getString(NAME).toString(), data)
        }

      /*  db.collection("medcenters").document(arguments?.getString(NAME).toString())
            .set(data, SetOptions.merge())*/
    }

    private fun updateFireBaseRatingMinusOne() {
        val numberOfDislikes: Int = arguments?.getInt(NUMBEROFDISLIKES)!! + 1
        arguments?.putInt(NUMBEROFDISLIKES, numberOfDislikes)
        myViewModel.numberOfDislikes.value = numberOfDislikes

        val numberOfLikes: Int = arguments?.getInt(NUMBEROFLIKES)!!


        val data = hashMapOf(
            "rating" to rating(numberOfLikes, numberOfDislikes),
            "numberOfDislikes" to numberOfDislikes
        )
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            myViewModel.updateData(arguments?.getString(NAME).toString(), data)
        }

       /* db.collection("medcenters").document(arguments?.getString(NAME).toString())
            .set(data, SetOptions.merge())*/
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