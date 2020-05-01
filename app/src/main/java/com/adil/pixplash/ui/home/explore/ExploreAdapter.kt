package com.adil.pixplash.ui.home.explore

import android.content.Context
import android.content.Intent
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
import com.adil.pixplash.data.local.db.entity.Link
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.local.db.entity.Url
import com.adil.pixplash.ui.home.HomeActivity
import com.airbnb.lottie.LottieAnimationView
import com.squareup.picasso.Picasso.get
import kotlinx.android.synthetic.main.footer_view.view.*
import kotlinx.android.synthetic.main.grid_item_image.view.*
import kotlinx.android.synthetic.main.header_view.view.*
import kotlinx.android.synthetic.main.header_view.view.tvLatest


class ExploreAdapter(
    val context: Context,val orderByClickListener: (String) -> Unit, val reloadListener: (Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
        const val TYPE_FOOTER = 2
    }

    private var isFooterEnabled = true

    private var isRetryFooter = false

    private var list : MutableList<Photo> = mutableListOf()

    init {
        //list.add(PhotoResponse("","","","",Urls("","","")))
        list.add(Photo(0,"","","","",
            Url("","","","","")
            ,Link(""),""))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER ->
                HeaderViewHolder(layoutInflater.inflate(R.layout.header_view, parent, false), context)
            TYPE_ITEM -> ViewHolder(layoutInflater.inflate(R.layout.grid_item_image, parent, false))
            else -> FooterView(layoutInflater.inflate(R.layout.footer_view, parent, false))
        }
    }

    override fun getItemCount(): Int = if (isFooterEnabled) list.size + 1 else list.size

    override fun getItemId(position: Int): Long {
        return if(position==0) super.getItemId(position) else list[position-1].photoId.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(holder)
    }

    private fun bind(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is HeaderViewHolder -> {
                val layoutParams =
                    StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                layoutParams.isFullSpan = true
                holder.itemView.layoutParams = layoutParams
            }
            is ViewHolder -> {
                val image = list[holder.adapterPosition].urls.small
                get().load(image).into(holder.imageView)
            }
            is FooterView -> {
                val layoutParams =
                    StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                layoutParams.isFullSpan = true
                holder.itemView.layoutParams = layoutParams
                if (isRetryFooter) {
                    holder.loadingView.visibility = View.GONE
                    holder.cardRetry.visibility = View.VISIBLE
                } else {
                    holder.loadingView.visibility = View.VISIBLE
                    holder.cardRetry.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position==0) TYPE_HEADER
        else if (isFooterEnabled && position >= list.size) TYPE_FOOTER
        else TYPE_ITEM
    }

    /**
     * Enable or disable footer (Default is true)
     *
     * @param isEnabled boolean to turn on or off footer.
     */
    fun enableFooter(isEnabled: Boolean) {
        isFooterEnabled = isEnabled
    }



    fun enableFooterRetry(value: Boolean) {
        isRetryFooter = value
        notifyItemChanged(list.size-1)
    }

    fun appendList(list: List<Photo>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun resetList() {
        list.clear()
        list.add(Photo(0,"","","","",
            Url("","","","","")
            ,Link(""),""))
        notifyDataSetChanged()
    }

    /**
     * All ViewHolders (header, iem, footer)
     * */

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image
        init {
            imageView.setOnClickListener {
                context.startActivity(Intent(context as HomeActivity, ImageDetailActivity::class.java))
                context.overridePendingTransition(R.anim.slide_up, R.anim.nothing)
            }
        }
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
                orderByClickListener(activeOrder)
            }
            cardOldest.setOnClickListener{
                cardLatest.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                it.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_dark)
                cardPopular.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                tvLatest.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvOldest.setTextColor(ContextCompat.getColor(context, R.color.white))
                tvPopular.setTextColor(ContextCompat.getColor(context, R.color.black))
                activeOrder = "oldest"
                orderByClickListener(activeOrder)
            }
            cardPopular.setOnClickListener{
                cardLatest.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                cardOldest.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                it.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_dark)
                tvLatest.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvOldest.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvPopular.setTextColor(ContextCompat.getColor(context, R.color.white))
                activeOrder = "popular"
                orderByClickListener(activeOrder)
            }
        }
    }

    inner class FooterView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardRetry: FrameLayout = itemView.cardRetry
        val loadingView: LottieAnimationView = itemView.loadingView
        init {
            cardRetry.setOnClickListener {
                reloadListener(true)
            }
        }
    }
}