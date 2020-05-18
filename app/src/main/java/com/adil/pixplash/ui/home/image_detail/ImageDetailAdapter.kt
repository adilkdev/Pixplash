package com.adil.pixplash.ui.home.image_detail

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.adil.pixplash.R
import com.adil.pixplash.data.local.db.entity.Photo
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_full_image.view.*
import kotlinx.coroutines.*


class ImageDetailAdapter(val context: Context,
                         val job: CompletableJob
): RecyclerView.Adapter<ImageDetailAdapter.ImageDetailViewHolder>() {

    private val list: MutableList<Photo> = mutableListOf()

    lateinit var dismissListener: (value: Boolean) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDetailViewHolder {
        return ImageDetailViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_full_image, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ImageDetailViewHolder, position: Int) {
        val image = list[holder.adapterPosition].urls.regular
        Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.ivImage)
        //holder.tvAuthor.text = list[holder.adapterPosition].user.name
    }

    fun setTheDismissListener(dismissListener: (value: Boolean) -> Unit) {
        this.dismissListener = dismissListener
    }

    fun appendList(tempList: List<Photo>) {
        CoroutineScope(Dispatchers.IO + job).launch {
            list.addAll(tempList)
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
            }
        }
    }

    fun cancelAllJobs() {
        job.cancel()
    }

    inner class ImageDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ZoomageView = itemView.ivImage
        val ivDownload = itemView.ivDownload
//        val ivLink = itemView.ivLink
//        val tvAuthor = itemView.tvAuthor
//        val tvUnsplash = itemView.tvUnsplash

        init {
//            tvAuthor.paintFlags = Paint.UNDERLINE_TEXT_FLAG
//            tvUnsplash.setOnClickListener {
//                val link = "https://unsplash.com"
//                visitLink(link)
//            }
//            ivLink.setOnClickListener {
//                val link = "https://unsplash.com/photos/${list[adapterPosition].photoId}"
//                visitLink(link)
//            }
//            tvAuthor.setOnClickListener {
//                val link = "https://unsplash.com/@${list[adapterPosition].user.username}"
//                visitLink(link)
//            }

            ivDownload.setOnClickListener {
                download()
            }

            ivImage.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when(event?.action) {
                        MotionEvent.ACTION_UP -> {
                            if (ivImage.currentScaleFactor == 1f) {
                                dismissListener(true)
                                if (ivDownload.visibility==View.VISIBLE) {
                                    ivDownload.visibility = View.INVISIBLE
//                                    ivLink.visibility = View.INVISIBLE
//                                    tvAuthor.visibility = View.INVISIBLE
//                                    tvUnsplash.visibility = View.INVISIBLE
                                } else {
                                    ivDownload.visibility = View.VISIBLE
//                                    ivLink.visibility = View.VISIBLE
//                                    tvAuthor.visibility = View.VISIBLE
//                                    tvUnsplash.visibility = View.VISIBLE
                                }
                            } else {
                                dismissListener(false)
                                ivDownload.visibility = View.INVISIBLE
//                                ivLink.visibility = View.INVISIBLE
//                                tvAuthor.visibility = View.INVISIBLE
//                                tvUnsplash.visibility = View.INVISIBLE
                            }
                        }
                    }
                    return false
                }

            })
        }

        private fun download() {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // this will request for permission when user has not granted permission for the app
                ActivityCompat.requestPermissions(
                    context as ImageDetailActivity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )
            } else {
                //Download Script
                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val uri: Uri = Uri.parse(list[adapterPosition].download.download)
                val request = DownloadManager.Request(uri)
                request.setVisibleInDownloadsUi(true)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    list[adapterPosition].photoId + ".jpg"
                )
                downloadManager.enqueue(request)
            }
        }

        fun visitLink(link: String) {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(browserIntent)
        }

    }
}