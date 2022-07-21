package com.adil.pixplash.ui.home.collection.activity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.data.local.db.entity.Link
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.local.db.entity.Url
import com.adil.pixplash.data.remote.response.User
import com.adil.pixplash.ui.home.explore.ExploreAdapter
import com.adil.pixplash.ui.home.image_detail.ImageDetailActivity
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.view_helpers.DynamicHeightNetworkImageView
import com.airbnb.lottie.LottieAnimationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.footer_view.view.*
import kotlinx.android.synthetic.main.grid_item_image.view.*
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CollectionPhotoAdapter(
    val context: Context, val job: CompletableJob
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
        const val TYPE_FOOTER = 2
    }

    lateinit var reloadListener: (Boolean) -> Unit
    lateinit var savePhotoListener: (List<Photo>) -> Unit

    private var isFooterEnabled = true
    private var isRetryFooter = false
    private var errorString: String = ""

    private var list : MutableList<Photo> = mutableListOf()

    private var page: Int = 0

    private var collectionId: String = ""

    init {
        list.add(Photo(0,"","","","",
            Url("","","","","")
            , Link(""),"", AppConstants.PHOTO_TYPE_COLLECTION, User("","")
        ))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ExploreAdapter.TYPE_HEADER ->
                HeaderViewHolder(
                    layoutInflater.inflate(R.layout.header_view_blank, parent, false)
                )
            ExploreAdapter.TYPE_ITEM -> ViewHolder(
                layoutInflater.inflate(
                    R.layout.grid_item_image,
                    parent,
                    false
                )
            )
            else -> FooterView(
                layoutInflater.inflate(
                    R.layout.footer_view,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = if (isFooterEnabled) list.size + 1 else list.size

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
                val item = list[holder.adapterPosition]
                val image = item.urls.small
                Picasso.get().load(image)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView)
                val aspectRatio = item.width.toFloat() / item.height.toFloat()
                holder.imageView.setAspectRatio(aspectRatio = aspectRatio)
                holder.imageView.requestLayout()
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
                    holder.tvError.visibility = View.VISIBLE
                    holder.tvError.text = errorString
                } else {
                    holder.tvError.visibility = View.GONE
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
     * @param isFooterEnabled boolean to turn on or off footer.
     */

    fun enableFooterRetry(value: Boolean, errorString: String?) {
        isRetryFooter = value
        if (errorString!=null)
            this.errorString = errorString
        notifyDataSetChanged()
    }

    fun enableFooter(value: Boolean) {
        isFooterEnabled = value
        notifyDataSetChanged()
    }

    fun setCollectionId(id: String) {
        collectionId = id
    }

    fun appendList(appendThisList: List<Photo>, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            this@CollectionPhotoAdapter.page = page
            list.addAll(appendThisList)
            savePhotoListener(appendThisList)
        }
        notifyDataSetChanged()
    }

    /**
     * Cancel all the background tasks while removing adapter
     */

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        job.cancel()
        super.onDetachedFromRecyclerView(recyclerView)
    }


    /**
     * Setting all listeners
     */

    fun setTheReloadListener(listener: (Boolean) -> Unit) {
        this.reloadListener = listener
    }

    fun setSavePhotoInDBListener(savePhotoListener: (List<Photo>) -> Unit) {
        this.savePhotoListener = savePhotoListener
    }


    /**
     * All ViewHolders (header, iem, footer)
     * */

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: DynamicHeightNetworkImageView = itemView.image
        init {
            imageView.setOnClickListener {
                if (adapterPosition!=-1) {
                    context.startActivity(
                        Intent(context as CollectionPhotosActivity, ImageDetailActivity::class.java)
                            .putExtra(AppConstants.ADAPTER_POSITION_PHOTO_ID, list[adapterPosition].photoId)
                            .putExtra(AppConstants.LOADED_PAGES, page)
                            .putExtra(AppConstants.ACTIVE_ORDER, ExploreAdapter.activeSortingOrder)
                            .putExtra("type",AppConstants.PHOTO_TYPE_COLLECTION)
                            .putExtra("collectionId", collectionId)

                    )
                    context.overridePendingTransition(R.anim.slide_up, R.anim.nothing)
                }
            }
        }
    }

    inner class FooterView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardRetry: FrameLayout = itemView.cardRetry
        val loadingView: LottieAnimationView = itemView.loadingView
        val tvError: TextView = itemView.tvError
        init {
            cardRetry.setOnClickListener {
                reloadListener(true)
            }
        }
    }

}