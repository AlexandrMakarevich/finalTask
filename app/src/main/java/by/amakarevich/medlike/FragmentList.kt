package by.amakarevich.medlike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import by.amakarevich.medlike.adapter.AdapterMed
import by.amakarevich.medlike.adapter.OnClickListenerRating
import by.amakarevich.medlike.data.MedCenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class FragmentList : Fragment(), OnClickListenerRating {

    private val mFirebaseAuth: FirebaseAuth
        get() {
            return FirebaseAuth.getInstance()
        }
    private val mFirebaseUser: FirebaseUser?
        get() {
            return mFirebaseAuth.currentUser
        }

    private val myViewModel: ViewModelFireBase by activityViewModels()
    private val adapterMed = AdapterMed(this)
    private var state = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MyLog", "OnCreate FragmentList")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        Log.d("MyLog", "OnCreateView FragmentList")
        val recyclerView: RecyclerView? = view?.findViewById(R.id.recyclerView)
        recyclerView?.apply {
            this.adapter = adapterMed
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyLog", "OnViewCreated FragmentList")


        myViewModel.data.observe(viewLifecycleOwner, Observer {
            val list: MutableList<MedCenter> = mutableListOf()
            for (document in it) {
                val medCenter = document.toObject(MedCenter::class.java)
                list.add(medCenter)
            }
            Log.d("MyLog", list.toString())

            if (state) {
                adapterMed.addItems(list)
            }
            state = false
            it ?: return@Observer
        })
    }

    override fun onItemClick(
        imageUrl: String,
        name: String,
        numberOfLikes: Int?,
        numberOfDislikes: Int?,
        rating: Int?
    ) {
        Log.d("MyLog", imageUrl)

        if (mFirebaseUser == null) {
            Log.d("MyLog", "mFirebaseUser == null")
            val intent = Intent(context, SignInActivity::class.java)
            intent.putExtra("ImageUrl", imageUrl)
            intent.putExtra("Name", name)
            intent.putExtra("NumberOfLikes", numberOfLikes)
            intent.putExtra("NumberOfDislikes", numberOfDislikes)
            intent.putExtra("Rating", rating)
            startActivity(intent)

        } else {
            val list = mutableListOf(imageUrl, name, numberOfLikes, numberOfDislikes, rating)
            myViewModel.currentMedCenter.value = list
            Log.d("MyLog", mFirebaseUser!!.uid) // уникальный идентификатор пользователя
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentList().apply {
                arguments = Bundle().apply {
                    putString(Companion.ARG_PARAM, "")
                }
            }

        private const val ARG_PARAM = "param"
    }
}


// add Medcenters===================================================================================
/*val button = view.findViewById<Button>(R.id.buttonAdd)
val db = Firebase.firestore
val list = mutableListOf<MedCenter>()
button.setOnClickListener {
    val medcenter = MedCenter(
        "ЛОДЭ",
        "Минск, пр-т Независимости, 58",
        5,
        "https://www.lode.by/local/assets/images/logo.png",
        3,
        5
    )
    list.add(medcenter)
    val medcenter1 = MedCenter(
        "Нордин",
        "Минск, ул. Сурганова, 47Б ",
        7,
        "https://ms1.103.by/images/80c4e9b074738e185f00852f5de2b115/resize/w=250,h=82,q=94/place_logo/05/b0/c9/05b0c98d9f501b1706cb011c889aafd7.jpg",
        4,
        15
    )
    list.add(medcenter1)
    val medcenter2 = MedCenter(
        "Кравира",
        "Минск, пр-т. Победителей, 45 ",
        3,
        "https://kravira.by/wp-content/uploads/2013/11/logo1.jpg",
        17,
        100
    )
    list.add(medcenter2)
    list.forEach {
        db.collection("medcenters").document(it.name.toString()).set(it)
    }

}*/
// add Medcenters===================================================================================


