package com.adil.pixplash.utils.view_helpers

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.adil.pixplash.R
import kotlinx.coroutines.*


class ClippedBanner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val path = Path()
    private var bitmap: Bitmap? = null
    private lateinit var tempCanvas: Canvas
    private lateinit var tempBitmap: Bitmap

    //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
    val filter: ColorFilter = LightingColorFilter(-0x525252, 0x00000000) // darken

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        tempCanvas = Canvas(tempBitmap)
        tempCanvas.drawRoundRect(
            0f, 0f, width.toFloat(), height.toFloat(), radius, radius, paint
        )
        tempCanvas.drawRect(0f, 0f, width.toFloat(), 150f, paint)
        drawCombinedClipping(canvas!!)
    }

    private val radius = dpToPx(0).toFloat()

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        invalidate()
    }

    private fun drawCombinedClipping(canvas: Canvas){

        if (bitmap==null)
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.placeholder)
        CoroutineScope(Dispatchers.Default).launch {
            val job = async { scaleCenterCrop(bitmap!!, height, width) }
            job.await()
            bitmap = job.getCompleted()
        }
        paint.colorFilter = filter
        canvas.drawBitmap(tempBitmap, 0f, 0f, paint) // Destination
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap!!, 0f, 0f, paint) // Source
        paint.xfermode = null
    }

    private fun scaleCenterCrop(source: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
        val sourceWidth = source.width
        val sourceHeight = source.height

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        val xScale = newWidth.toFloat() / sourceWidth
        val yScale = newHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        // Now get the size of the source bitmap when scaled
        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        val left = (newWidth - scaledWidth) / 2
        val top = (newHeight - scaledHeight) / 2

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        val dest = Bitmap.createBitmap(newWidth, newHeight, source.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, null)
        return dest
    }

    fun View.dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return ((dp * displayMetrics.density) + 0.5).toInt()
    }

    fun View.pxToDp(px: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return ((px/displayMetrics.density)+0.5).toInt()
    }

}