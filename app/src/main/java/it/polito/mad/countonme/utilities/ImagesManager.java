package it.polito.mad.countonme.utilities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by francescobruno on 20/05/17.
 */

public final class ImagesManager {


    public static Drawable resizeImage(Resources resources, int imageResId, float factor ) {
        Bitmap bitmap = BitmapFactory.decodeResource( resources, imageResId );
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap,
                (int) (bitmap.getWidth() * factor), (int) (bitmap.getHeight() * factor ), false);
        return new BitmapDrawable( resources, bitmapResized );
    }

}
