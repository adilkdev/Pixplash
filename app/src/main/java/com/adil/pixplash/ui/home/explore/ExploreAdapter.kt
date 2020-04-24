package com.adil.pixplash.ui.home.explore

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.data.remote.response.PhotoResponse
import com.adil.pixplash.data.remote.response.Urls
import com.squareup.picasso.Picasso.get
import kotlinx.android.synthetic.main.grid_item_image.view.*
import kotlinx.android.synthetic.main.header_view.view.*


class ExploreAdapter(
    val context: Context,open val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
        const val TYPE_FOOTER = 2
    }

    private var list : MutableList<PhotoResponse> = mutableListOf()

    init {
        list.add(PhotoResponse("","","","",Urls("","","")))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER ->
                HeaderViewHolder(layoutInflater.inflate(R.layout.header_view, parent, false), context)
            TYPE_ITEM -> ViewHolder(layoutInflater.inflate(R.layout.grid_item_image, parent, false))
            else -> ViewHolder(layoutInflater.inflate(R.layout.grid_item_image, parent, false))
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val layoutParams =
                StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            layoutParams.isFullSpan = true
            holder.itemView.layoutParams = layoutParams
        } else if (holder is ViewHolder) {
            val image = list[position].urls.small
            get().load(image).into(holder.imageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position==0) TYPE_HEADER else TYPE_ITEM
    }

    fun appendList(list: List<PhotoResponse>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun resetList() {
        list.clear()
        list.add(PhotoResponse("","","","",Urls("","","")))
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image
    }

    inner class HeaderViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        val cardLatest: FrameLayout = itemView.cardLatest
        val cardOldest: FrameLayout = itemView.cardOldest
        val cardPopular: FrameLayout = itemView.cardPopular
        val tvLatest: TextView = itemView.tvLatest
        val tvOldest: TextView = itemView.tvOldest
        val tvPopular: TextView = itemView.tvPopular

        var activeOrder = "latest"

        init {
            cardLatest.setOnClickListener{
                it.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_dark)
                cardOldest.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                cardPopular.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                tvLatest.setTextColor(ContextCompat.getColor(context, R.color.white))
                tvOldest.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvPopular.setTextColor(ContextCompat.getColor(context, R.color.black))
                activeOrder = "latest"
                itemClickListener(activeOrder)
            }
            cardOldest.setOnClickListener{
                cardLatest.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                it.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_dark)
                cardPopular.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                tvLatest.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvOldest.setTextColor(ContextCompat.getColor(context, R.color.white))
                tvPopular.setTextColor(ContextCompat.getColor(context, R.color.black))
                activeOrder = "oldest"
                itemClickListener(activeOrder)
            }
            cardPopular.setOnClickListener{
                cardLatest.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                cardOldest.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                it.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_dark)
                tvLatest.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvOldest.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvPopular.setTextColor(ContextCompat.getColor(context, R.color.white))
                activeOrder = "popular"
                itemClickListener(activeOrder)
            }
        }
    }

    class FooterView(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}