package it.polito.mad.countonme.networking;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import it.polito.mad.countonme.R;

/**
 * Asyns task implementation to load images from the server
 * Created by francescobruno on 05/04/17.
 */

public class ImageFromUrlTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> mImgViewRef;
    private final int mDefImageResId;
    private final boolean mRoundIt;

    public ImageFromUrlTask( ImageView imgView, int defImageResId, boolean roundIt ) {
        mImgViewRef = new WeakReference<ImageView>( imgView );
        mDefImageResId = defImageResId;
        mRoundIt = roundIt;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap( params[0] );
    }

    @Override
    protected void onPostExecute( Bitmap bitmap ) {
        if( isCancelled() ) bitmap = null;

        if( mImgViewRef != null ) {
            ImageView imageView = mImgViewRef.get();
            if( imageView != null ) {
                if( bitmap != null ) {
                    if( mRoundIt == true ) {
                        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create( imageView.getResources(), bitmap );
                        roundedBitmapDrawable.setCornerRadius( imageView.getWidth() / 2.0f );
                        roundedBitmapDrawable.setAntiAlias(true);
                        imageView.setImageDrawable( roundedBitmapDrawable );
                    }
                    else
                        imageView.setImageBitmap( bitmap );
                } else {
                    Drawable defaultImage = imageView.getContext().getResources().getDrawable( mDefImageResId, null );
                    imageView.setImageDrawable( defaultImage );
                }
            }
        }
    }

    private Bitmap downloadBitmap( String url ) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK ) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageFromUrlTask", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
