package it.polito.mad.countonme.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.business.ImageManagement;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.storage.StorageManager;

/**
 * Custmom RecycleView Adapter for the sharing activities list
 * Created by francescobruno on 03/04/17.
 */

public class SharingActivitiesAdapter extends RecyclerView.Adapter<SharingActivitiesAdapter.ShActViewHolder> {

    public static class ShActViewHolder extends RecyclerView.ViewHolder {
        @BindView( R.id.sharing_activity_img ) ImageView mIvPhoto;
        @BindView( R.id.sharing_activity_name) TextView mTvName;
        @BindView( R.id.sharing_activity_desc ) TextView mTvDesc;

        private StorageReference mStorageRef;

        public ShActViewHolder(View itemView ) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }

        public void setData(final SharingActivity activity, final IOnListItemClickListener listener ) {
            String namePhoto;
            String imgUrl = activity.getImageUrl();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            if( imgUrl != null && imgUrl.length() > 0 ) {
                namePhoto = StorageManager.STORAGE_SHAREACTS_FOLDER + "/" + activity.getKey();
                StorageReference newstoragereference = mStorageRef.child(namePhoto);

                Glide.with(mIvPhoto.getContext()).using(new ImageManagement()).load(newstoragereference).asBitmap().centerCrop().into(new BitmapImageViewTarget(mIvPhoto) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mIvPhoto.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mIvPhoto.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else {
                 mIvPhoto.setImageResource(R.drawable.img_sharing_default);
            }

            mTvName.setText( activity.getName() );
            mTvDesc.setText( activity.getDescription() );

            // attach the listener to the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( listener != null )
                        listener.onItemClick( activity );
                }
            });
        }
    }


    private List<SharingActivity> mSharingActivities;
    private LayoutInflater mInflater;
    private IOnListItemClickListener mListener;

    public SharingActivitiesAdapter(Context context, List<SharingActivity> data, IOnListItemClickListener listener ) {
        mSharingActivities = data;
        mListener = listener;
        mInflater = LayoutInflater.from( context );
    }

    @Override
    public ShActViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.sharing_activities_list_item, parent, false);
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
