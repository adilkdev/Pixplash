package com.adil.pixplash.ui.home.explore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.adil.pixplash.R
import com.adil.pixplash.data.remote.response.PhotoResponse
import com.squareup.picasso.Picasso.get
import kotlinx.android.synthetic.main.grid_item_image.view.*


class ExploreAdapter(val context: Context) : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {

    private var list : MutableList<PhotoResponse> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.grid_item_image, parent, false)
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = list[position].urls.small
        get().load(image).into(holder.imageView)
    }

    fun appendList(list: List<PhotoResponse>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image
    }
}