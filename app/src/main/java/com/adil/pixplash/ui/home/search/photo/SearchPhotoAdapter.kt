package com.adil.pixplash.ui.home.search.photo

import android.content.Context
import android.content.Intent
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
import com.adil.pixplash.data.remote.response.User
import com.adil.pixplash.ui.home.HomeActivity
import com.adil.pixplash.ui.home.image_detail.ImageDetailActivity
import com.adil.pixplash.ui.home.search.SearchActivity
import com.adil.pixplash.utils.AppConstants
import com.airbnb.lottie.LottieAnimationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.footer_view.view.*
import kotlinx.android.synthetic.main.grid_item_image.view.*
import kotlinx.android.synthetic.main.header_view.view.*
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchPhotoAdapter(
    val context: Context,
    val job: CompletableJob
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM = 1
        const val TYPE_FOOTER = 2
    }

    lateinit var reloadListener: (Boolean) -> Unit
    lateinit var savePhotoListener: (List<Photo>) -> Unit
    lateinit var removePhotoListener: (Boolean) -> Unit

    private var isFooterEnabled = false
    private var isRetryFooter = false
    private var errorString: String = ""

    private var list : MutableList<Photo> = mutableListOf()

    private var page: Int = 0

    private var query = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ITEM -> ViewHolder(layoutInflater.inflate(R.layout.grid_item_image, parent, false))
            else -> FooterView(layoutInflater.inflate(R.layout.footer_view, parent, false))
        }
    }

    override fun getItemCount(): Int = if (isFooterEnabled) list.size + 1 else list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(holder)
    }

    private fun bind(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is ViewHolder -> {
                val image = list[holder.adapterPosition].urls.small
                Picasso.get().load(image).into(holder.imageView)
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

    override fun getItemViewType(position: Int): Int =
        if (isFooterEnabled && position >= list.size) TYPE_FOOTER
        else TYPE_ITEM

    /**
     * Enable or disable footer (Default is true)
     *
     * @param isEnabled boolean to turn on or off footer.
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

    fun appendList(appendThisList: List<Photo>, page: Int) {
        CoroutineScope(Dispatchers.IO + job).launch {
            this@SearchPhotoAdapter.page = page
            list.addAll(appendThisList)
            savePhotoListener(appendThisList)
        }
        notifyDataSetChanged()
    }

    fun resetList() {
        CoroutineScope(Dispatchers.IO + job).launch {
            list.clear()
            removePhotoListener(true)
        }
        notifyDataSetChanged()
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

    fun setRemovePhotoInDBListener(removePhotoListener: (Boolean) -> Unit) {
        this.removePhotoListener = removePhotoListener
    }

    fun setQuery(query: String) {
        this.query = query
    }

    /**
     * Cancel all the background tasks while removing adapter
     */

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        job.cancel()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    /**
     * All ViewHolders (header, iem, footer)
     * */

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image
        init {
            imageView.setOnClickListener {
                context.startActivity(
                    Intent(context as SearchActivity, ImageDetailActivity::class.java)
                    .putExtra(AppConstants.ADAPTER_POSITION_PHOTO_ID, list[adapterPosition].photoId)
                    .putExtra(AppConstants.LOADED_PAGES, page)
                    .putExtra("type", AppConstants.PHOTO_TYPE_SEARCH)
                    .putExtra("query",query)
                )
                context.overridePendingTransition(R.anim.slide_up, R.anim.nothing)
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
