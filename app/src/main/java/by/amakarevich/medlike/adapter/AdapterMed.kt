package by.amakarevich.medlike.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.amakarevich.medlike.R
import by.amakarevich.medlike.data.MedCenter
import coil.api.load


class AdapterMed(private val onClickListenerRating: OnClickListenerRating) :
    RecyclerView.Adapter<ViewHolderMed>() {

    private val items = mutableListOf<MedCenter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMed {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, null)
        Log.d("MyLog", "onCreateViewHolder")
        return ViewHolderMed(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolderMed, position: Int) {
        holder.textViewName.text = items[position].name
        holder.textViewAddress.text = items[position].address
        holder.textViewRating.text = items[position].rating.toString()
        holder.imageMedCenter.load(items[position].image)
        holder.imageRating.setOnClickListener {
            onClickListenerRating.onItemClick(
                items[position].image.toString(),
                items[position].name.toString(),
                items[position].numberOfLikes,
                items[position].numberOfDislikes,
                items[position].rating
            )
        }
    }

    fun addItems(newItems: List<MedCenter>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

class ViewHolderMed(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val textViewName = itemView.findViewById<TextView>(R.id.name)
    val textViewAddress = itemView.findViewById<TextView>(R.id.address)
    val textViewRating = itemView.findViewById<TextView>(R.id.rating)
    val imageMedCenter = itemView.findViewById<ImageView>(R.id.image_medcenter)
    val imageRating = itemView.findViewById<ImageView>(R.id.image_rating)

}

interface OnClickListenerRating {
    fun onItemClick(
        imageUrl: String,
        name: String,
        numberOfLikes: Int?,
        numberOfDislikes: Int?,
        rating: Int?
    )
}
