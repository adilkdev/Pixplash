package com.adil.pixplash.ui.home.search.collection

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adil.pixplash.R
import com.adil.pixplash.data.remote.response.Collection
import com.adil.pixplash.ui.home.HomeActivity
import com.adil.pixplash.ui.home.collection.activity.CollectionPhotosActivity
import com.adil.pixplash.ui.home.search.SearchActivity
import com.airbnb.lottie.LottieAnimationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_item.view.*
import kotlinx.android.synthetic.main.footer_view.view.*
import kotlinx.coroutines.*

class SearchCollectionAdapter(val context: Context,
                        val job: CompletableJob
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    companion object {
        const val TYPE_ITEM = 1
        const val TYPE_FOOTER = 2
    }

    lateinit var reloadListener: (Boolean) -> Unit

    private var isFooterEnabled = false
    private var isRetryFooter = false
    private var errorString: String = ""

    private var list : MutableList<Collection> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ITEM ->
                CollectionViewHolder(layoutInflater.inflate(R.layout.collection_item, parent, false))
            else ->
                FooterView(layoutInflater.inflate(R.layout.footer_view, parent, false))
        }
    }

    override fun getItemCount(): Int = if (isFooterEnabled) list.size + 1 else list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(holder)
    }

    private fun bind(holder: RecyclerView.ViewHolder) {
        when (holder) {
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

    override fun getItemViewType(position: Int): Int =
        if (isFooterEnabled && position >= list.size) TYPE_FOOTER
        else TYPE_ITEM

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
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
            }
        }
    }

    fun resetList() {
        CoroutineScope(Dispatchers.IO).launch {
            list.clear()
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
                (context as SearchActivity).overridePendingTransition(R.anim.slide_up, R.anim.nothing)
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