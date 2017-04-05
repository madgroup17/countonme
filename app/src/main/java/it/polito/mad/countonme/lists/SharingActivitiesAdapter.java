package it.polito.mad.countonme.lists;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.polito.mad.countonme.R;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Custmom RecycleView Adapter for the sharing activities list
 * Created by francescobruno on 03/04/17.
 */

public class SharingActivitiesAdapter extends RecyclerView.Adapter<SharingActivitiesAdapter.ShActViewHolder> {

    private List<SharingActivity> mSharingActivities;
    private LayoutInflater mInflater;

    public static class ShActViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgView;
        TextView  mTextView;

        ShActViewHolder(View itemView ) {
            super( itemView );
            mImgView = (ImageView) itemView.findViewById(R.id.sharing_activity_img);
            mTextView = (TextView) itemView.findViewById( R.id.sharing_activity_name );
        }

        public void setData( SharingActivity activity) {
            String imgUrl = activity.getImageUrl();

            if( imgUrl != null && imgUrl.length() > 0 )
                mImgView.setImageURI( Uri.parse( imgUrl ) );
            else
                mImgView.setImageResource(R.drawable.img_sharing_default);

            mTextView.setText( activity.getName() );
        }
    }

    public SharingActivitiesAdapter(Context context, List<SharingActivity> data ) {
        mSharingActivities = data;
        mInflater = LayoutInflater.from( context );
    }

    @Override
    public ShActViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.sharing_activity_item_list, parent, false);
        return new ShActViewHolder( view );
    }

    @Override
    public void onBindViewHolder(ShActViewHolder holder, int position) {
        holder.setData( mSharingActivities.get( position )  );
    }

    @Override
    public int getItemCount() {
        return mSharingActivities.size();
    }
}
