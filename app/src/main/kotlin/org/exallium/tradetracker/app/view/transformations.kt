package org.exallium.tradetracker.app.view.transformations

import android.graphics.*
import com.squareup.picasso.Transformation

public class CircleTransformation : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.getWidth(), source.getHeight())

        val x = (source.getWidth() - size) / 2
        val y = (source.getHeight() - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, source.getConfig())

        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.setShader(shader)
        paint.setAntiAlias(true)

        val r = size.toFloat() / 2.toFloat()
        canvas.drawCircle(r, r, r, paint)

        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}

public class MTGCardCropTransformation : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(source, xPixOffset, yPixOffset, width, height)
        if (result != source)
            source.recycle()
        return result
    }

    override fun key(): String {
        return "mtgTransform()"
    }

    companion object {
        private val cardRatio = 223.toFloat() / 310.toFloat()
        private val yPixOffset = 37
        private val xPixOffset = 21
        private val width = 185
        private val height = 134
    }
}