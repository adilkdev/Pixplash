package com.adil.pixplash.ui.home.explore

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adil.pixplash.R
import com.adil.pixplash.data.local.db.entity.Photo
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_full_image.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageDetailAdapter(val context: Context
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
        Picasso.get().load(image).into(holder.ivImage)
        //Glide.with(context).load("url").into(holder.ivImage)
    }

    fun setTheDismissListener(dismissListener: (value: Boolean) -> Unit) {
        this.dismissListener = dismissListener
    }

    fun appendList(tempList: List<Photo>) {
        CoroutineScope(Dispatchers.IO).launch {
            list.addAll(tempList)
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
            }
        }
    }

    inner class ImageDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ZoomageView = itemView.ivImage
        val bottomSheet = itemView.bottom_sheet
        var bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        init {
            bottomSheet.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            ivImage.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (ivImage.currentScaleFactor == 1f) {
                        dismissListener(true)
                    } else
                        dismissListener(false)

                    return false
                }

            })
//            ivImage.setOnTouchListener(object : View.OnTouchListener {
//                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                    //Log.e("adil", "touch")
//                    when (event!!.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            y1 = event!!.y
//                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
////                            Log.e("adil", "y1 = $y1")
//                        }
//                        MotionEvent.ACTION_UP -> {
//                            y2 = event!!.y
////                            Log.e("adil", "y2 = $y2")
//                            val deltaY: Float = y2 - y1
//                            Log.e("adil", "deltaY = $deltaY")
//                            if ((deltaY) > MIN_DISTANCE) {
////                                Log.e("adil", "top2bottom swipe")
//                                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
//                                dismissListener(true)
//                                //else
//                                    //bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                            } else if (deltaY < -MIN_DISTANCE){
//                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                            }
//                        }
//                    }
//                    return false
//                }
//
//            })
        }
    }
}