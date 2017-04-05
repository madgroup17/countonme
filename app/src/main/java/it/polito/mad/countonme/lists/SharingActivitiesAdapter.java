package it.polito.mad.countonme.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import it.polito.mad.countonme.R;
import it.polito.mad.countonme.interfaces.OnListItemClickListener;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.networking.ImageFromUrlTask;

/**
 * Custmom RecycleView Adapter for the sharing activities list
 * Created by francescobruno on 03/04/17.
 */

public class SharingActivitiesAdapter extends RecyclerView.Adapter<SharingActivitiesAdapter.ShActViewHolder> {

    public static class ShActViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgView;
        TextView  mTextView;

        ShActViewHolder(View itemView ) {
            super( itemView );
            mImgView = (ImageView) itemView.findViewById(R.id.sharing_activity_img);
            mTextView = (TextView) itemView.findViewById( R.id.sharing_activity_name );
        }

        public void setData(final SharingActivity activity, final OnListItemClickListener listener ) {
            String imgUrl = activity.getImageUrl();

            if( imgUrl != null && imgUrl.length() > 0 ) {
                new ImageFromUrlTask( mImgView ).execute( imgUrl );
            } else {
                mImgView.setImageResource(R.drawable.img_sharing_default);
            }

            mTextView.setText( activity.getName() );

            // attach the listener to the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick( activity );
                }
            });
        }
    }


    private List<SharingActivity> mSharingActivities;
    private LayoutInflater mInflater;
    private OnListItemClickListener mListener;

    public SharingActivitiesAdapter(Context context, List<SharingActivity> data, OnListItemClickListener listener ) {
        mSharingActivities = data;
        mListener = listener;
        mInflater = LayoutInflater.from( context );
    }

    @Override
    public ShActViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.sharing_activity_item_list, parent, false);
        return new ShActViewHolder( view );
    }

    @Override
    public void onBindViewHolder(ShActViewHolder holder, int position) {
        holder.setData( mSharingActivities.get( position ), mListener  );
    }

    @Override
    public int getItemCount() {
        return mSharingActivities.size();
    }
}
