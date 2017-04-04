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

public class SharingActivitiesAdapter extends RecyclerView.Adapter<SharingActivitiesAdapter.ViewHolder> {

    private List<SharingActivity> mSharingActivities;
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgView;
        TextView  mTextView;

        ViewHolder(View itemView ) {
            super( itemView );
            mImgView = (ImageView) itemView.findViewById(R.id.sharing_activity_img);
            mTextView = (TextView) itemView.findViewById( R.id.sharing_activity_name );
        }

        public void setData( SharingActivity activity, int positon ) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.sharing_activity_item_list, parent, false);
        ViewHolder holder = new ViewHolder( view );
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData( mSharingActivities.get( position ), position );
    }

    @Override
    public int getItemCount() {
        if( mSharingActivities == null )
            return 0;
        return mSharingActivities.size();
    }
}
