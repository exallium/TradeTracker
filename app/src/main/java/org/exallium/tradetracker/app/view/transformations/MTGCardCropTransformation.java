package org.exallium.tradetracker.app.view.transformations;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

public class MTGCardCropTransformation implements Transformation {

    private static final float cardRatio = 223f / 310f;
    private static final int yPixOffset = 37;
    private static final int xPixOffset = 21;
    private static final int width = 185;
    private static final int height = 134;

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap result = Bitmap.createBitmap(source, xPixOffset, yPixOffset, width, height);
        if (result != source)
            source.recycle();
        return result;
    }

    @Override
    public String key() {
        return "mtgTransform()";
    }
}
