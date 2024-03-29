package com.adil.pixplash.ui.home.collection.fragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adil.pixplash.R
import com.adil.pixplash.data.remote.response.Collection
import com.adil.pixplash.data.remote.response.CoverPhoto
import com.adil.pixplash.data.remote.response.Urls
import com.adil.pixplash.ui.home.HomeActivity
import com.adil.pixplash.ui.home.collection.activity.CollectionPhotosActivity
import com.adil.pixplash.ui.home.collection.fragment.CollectionFragment.Companion.TYPE_ALL
import com.adil.pixplash.ui.home.collection.fragment.CollectionFragment.Companion.TYPE_FEATURED
import com.adil.pixplash.ui.home.explore.ExploreAdapter
import com.adil.pixplash.ui.home.search.SearchActivity
import com.adil.pixplash.utils.view_helpers.ClippedBanner
import com.airbnb.lottie.LottieAnimationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_item.view.*
import kotlinx.android.synthetic.main.footer_view.view.*
import kotlinx.android.synthetic.main.footer_view.view.tvLatest
import kotlinx.android.synthetic.main.header_view.view.*
import kotlinx.coroutines.*
import java.lang.Exception

class CollectionAdapter(val context: Context,
                        val job: CompletableJob
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
        const val TYPE_FOOTER = 2
        var activeOrder = TYPE_ALL
    }

    private var url: String = ""
    lateinit var orderByClickListener: (Int) -> Unit
    lateinit var reloadListener: (Boolean) -> Unit

    private var isFooterEnabled = true
    private var isRetryFooter = false
    private var errorString: String = ""

    private var list : MutableList<Collection> = mutableListOf()

    init {
        list.add(Collection("","",0, CoverPhoto(
                Urls("","","")
        )))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ExploreAdapter.TYPE_HEADER ->
                HeaderViewHolder(layoutInflater.inflate(R.layout.header_view, parent, false), context)
            ExploreAdapter.TYPE_ITEM ->
                CollectionViewHolder(layoutInflater.inflate(R.layout.collection_item, parent, false))
            else ->
                FooterView(layoutInflater.inflate(R.layout.footer_view, parent, false))
        }
    }

    override fun getItemCount(): Int =
        if (isFooterEnabled) list.size + 1 else list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(holder)
    }

    private fun bind(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is HeaderViewHolder -> {
                if (url.isNotEmpty()) {
                    val iv = ImageView(context)
                    Picasso.get().load(url).into(iv, object: com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            holder.bannerView.setBitmap((iv.drawable as BitmapDrawable).bitmap)
                        }
                        override fun onError(e: Exception?) {}
                    })
                }
            }
            is CollectionViewHolder -> {
                val item = list[holder.adapterPosition]
                val image = item.coverPhoto.urls.regular
                Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.ivImage)
                holder.tvTitle.text = item.title
                holder.tvPhotoCount.text = "${item.photosCount} photos"
            }
            is FooterView -> {
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

    fun setBannerImage(url: String) {
        this.url = url
        notifyItemChanged(0)
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

    fun appendList(appendThisList: List<Collection>) {
        CoroutineScope(Dispatchers.IO + job).launch {
            list.addAll(appendThisList)
        }
        notifyDataSetChanged()
    }

    fun resetList() {
        CoroutineScope(Dispatchers.Default + job).launch {
            list.clear()
            list.add(
                Collection("","",0, CoverPhoto(
                    Urls("","","")
                )))
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
            }
        }
    }

    /**
     * Setting all listeners
     */

    fun setTheReloadListener(listener: (Boolean) -> Unit) {
        this.reloadListener = listener
    }

    fun setSortingListener(orderByClickListener: (Int) -> Unit) {
        this.orderByClickListener = orderByClickListener
    }

    /**
     * Cancel all the background tasks while removing adapter
     */

    fun cancelAllJobs() {
        job.cancel()
    }

    /**
     * All ViewHolders (header, iem, footer)
     * */

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage = itemView.ivImage
        val tvTitle = itemView.tvTitle
        val tvPhotoCount = itemView.tvPhotoCount

        init {
            ivImage.setOnClickListener {
                val intent = Intent(context, CollectionPhotosActivity::class.java)
                val item = list[adapterPosition]
                intent.putExtra("id", item.id)
                intent.putExtra("cover", item.coverPhoto.urls.regular)
                intent.putExtra("title", item.title)
                context.startActivity(intent)
                (context as HomeActivity).overridePendingTransition(R.anim.slide_up, R.anim.nothing)
            }
        }
    }

    inner class HeaderViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        val cardAll: FrameLayout = itemView.cardLatest
        val cardFeatured: FrameLayout = itemView.cardOldest
        val cardPopular: FrameLayout = itemView.cardPopular
        val tvAll: TextView = itemView.tvLatest
        val tvFeatured: TextView = itemView.tvOldest
        val bannerView: ClippedBanner = itemView.bannerView
        val searchView = itemView.searchView
        val tvBigTitle = itemView.tvBigTitle

        init {
            cardPopular.visibility = View.GONE
            tvAll.text = "All"
            tvFeatured.text = "Featured"
            tvBigTitle.text = "Collection"

            searchView.setOnClickListener {
//                val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                    context as HomeActivity,
//                    it,  // Starting view
//                    "search_transition" // The String
//                )
//                context?.startActivity(Intent(context, SearchActivity::class.java), options.toBundle())
                context?.startActivity(Intent(context, SearchActivity::class.java))
            }

            cardAll.setOnClickListener{
                it.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_dark)
                cardFeatured.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                tvAll.setTextColor(ContextCompat.getColor(context, R.color.white))
                tvFeatured.setTextColor(ContextCompat.getColor(context, R.color.black))
                activeOrder = TYPE_ALL
                orderByClickListener(activeOrder)
            }

            cardFeatured.setOnClickListener{
                cardAll.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_gray)
                it.background = ContextCompat.getDrawable(context, R.drawable.card_rounded_bg_dark)
                tvAll.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvFeatured.setTextColor(ContextCompat.getColor(context, R.color.white))
                activeOrder = TYPE_FEATURED
                orderByClickListener(activeOrder)
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