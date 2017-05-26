package it.polito.mad.countonme.lists;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.polito.mad.countonme.CountOnMeApp;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.SharingActivitiesListFragment;
import it.polito.mad.countonme.business.ImageManagement;
import it.polito.mad.countonme.database.DataLoader;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.database.ExpensesListLoader;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.messaging.MessagingManager;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.ReportBackAction;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;
import it.polito.mad.countonme.storage.StorageManager;

/**
 * Custmom RecycleView Adapter for the sharing activities list
 * Created by francescobruno on 03/04/17.
 */

public class SharingActivitiesAdapter extends RecyclerView.Adapter<SharingActivitiesAdapter.ShActViewHolder> {

    private int counter;

    public static class ShActViewHolder extends RecyclerView.ViewHolder {
        @BindView( R.id.sharing_activity_img ) ImageView mIvPhoto;
        @BindView( R.id.sharing_activity_name) TextView mTvName;
        @BindView( R.id.sharing_activity_desc ) TextView mTvDesc;
        View mItemView ;
        private StorageReference mStorageRef;

        public ShActViewHolder(View itemView ) {
            super( itemView );
            mItemView=itemView;
            ButterKnife.bind( this, itemView );
        }

        public void setData(final SharingActivity activity, final IOnListItemClickListener listener ) {
            String namePhoto;
            String imgUrl = activity.getImageUrl();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            if(SharingActivitiesAdapter.selection_list.size()!=0){
                for(SharingActivity shactaux : SharingActivitiesAdapter.selection_list){
                    if(shactaux.getKey().equals(activity.getKey())){
                        if(!mItemView.isSelected()){
                            mItemView.setSelected(true);
                            int color = Color.parseColor("#AAAAAAAA");
                            int colorWHITE = Color.parseColor("#FFFFFF");
                            mItemView.setBackgroundColor(color);
                            mIvPhoto.setBackgroundColor(color);
                            mItemView.setBackgroundColor(colorWHITE);
                            mTvName.setBackgroundColor(colorWHITE);
                            mTvDesc.setBackgroundColor(colorWHITE);
                        }
                    }else{
                        if(mItemView.isSelected()){
                            mItemView.setSelected(false);
                            int color = Color.parseColor("#FFFFFF");
                            mIvPhoto.setBackgroundColor(color);
                            mItemView.setBackgroundColor(color);
                        }
                    }
                }
            }
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
    public IOnListItemClickListener mListener;
    private int currentPosition;
    private SharingActivity infoData;
    private Context mContext;
    public Activity currentActivity;
    private ExpensesListLoader mExpensesListLoader;
    private String mCurrentUser;
    public SharingActivitiesListFragment sharingActivityListFragment;
    public static ArrayList<SharingActivity> selection_list;


    public List<SharingActivity> getmSharingActivities() {
        return mSharingActivities;
    }

    public void setmSharingActivities(List<SharingActivity> mSharingActivities) {
        this.mSharingActivities = mSharingActivities;
    }

    public SharingActivity getInfoData() {
        return infoData;
    }

    public void setInfoData(SharingActivity infoData) {
        this.infoData = infoData;
    }

    public SharingActivitiesAdapter(Context context, List<SharingActivity> data, IOnListItemClickListener listener, String currentUser ,SharingActivitiesListFragment sharingActivitiesListFragment, ArrayList<SharingActivity> selection_list){
        mSharingActivities = data;
        mListener = listener;
        mInflater = LayoutInflater.from( context );
        mCurrentUser=currentUser;
        sharingActivityListFragment = sharingActivitiesListFragment;
        this.selection_list=selection_list;
    }

    @Override
    public ShActViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.sharing_activities_list_item, parent, false);
        return new ShActViewHolder( view );
    }

    @Override
    public void onBindViewHolder(final ShActViewHolder holder, final int position) {
        SharingActivity sa = mSharingActivities.get(position);
        String key = sa.getKey();
        holder.setData( mSharingActivities.get( position ), mListener  );
        //new code for multiple selection for delete
        infoData = mSharingActivities.get(position);
        holder.mItemView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                infoData = mSharingActivities.get(position);
                Activity parentActivity  = sharingActivityListFragment.getActivity();
                ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_VIEW_SHARING_ACTIVITY_DETAIL_TABS, infoData.getKey() ));
            }
        });
        holder.mItemView.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                infoData = mSharingActivities.get(position);
                if(v.isSelected()){
                    v.setSelected(false);
                    int color = Color.parseColor("#FFFFFF");
                    holder.mIvPhoto.setBackgroundColor(color);
                    holder.mItemView.setBackgroundColor(color);
                }else{
                    v.setSelected(true);
                    int color = Color.parseColor("#AAAAAAAA");
                    int colorWHITE = Color.parseColor("#FFFFFF");
                    v.setBackgroundColor(color);
                    holder.mIvPhoto.setBackgroundColor(color);
                    holder.mItemView.setBackgroundColor(colorWHITE);
                    holder.mTvName.setBackgroundColor(colorWHITE);
                    holder.mTvDesc.setBackgroundColor(colorWHITE);
                }
                sharingActivityListFragment.prepareSelection(infoData);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSharingActivities.size();
    }

    public void updateAdapter(final ArrayList<SharingActivity> list) {
        counter = list.size();
        if(counter!=0) {
            AlertDialog.Builder mPopup = new AlertDialog.Builder(((SharingActivitiesListFragment) this.mListener).getActivity());//traer el activity
            mPopup.setIcon(R.drawable.appicon);
            mPopup.setTitle(R.string.lbl_deleteSharingActivity);
            mPopup.setMessage(R.string.lbl_confirmationdeleteSharingActivity);
            mPopup.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    while (counter != 0) {
                        Iterator<SharingActivity> iter = list.iterator();
                        if (iter.hasNext()) {
                            SharingActivity shrAct = iter.next();
                            SharingActivitiesListFragment.selection_list.remove(shrAct);
                            removeItemShAct(shrAct);
                            counter = list.size();
                        }
                    }
                    dialog.dismiss();
                }
            });
            mPopup.setNegativeButton(R.string.lbl_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = mPopup.create();
            alertDialog.show();
        }
        notifyDataSetChanged();
    }

    public void dimissSharingActivity(int position) {
        removeItemShAct(mSharingActivities.get(position));
        if(position==mSharingActivities.size()){
            position--;
        }
        mSharingActivities.remove(position);
        this.notifyItemRemoved(position);
    }

    private void removeItemShAct(SharingActivity infoData) {
        boolean unicmember = checkunicmember(infoData);//if true means unique user on sharing activity
        DatabaseReference dbRef = DataManager.getsInstance().getSharActExpensesReference(infoData.getKey());
        boolean activityHasExpRelated =false;
        activityHasExpRelated=DataManager.checkExpRelofShaAct(infoData.getKey());
//TODO: check if exist any expense related to this Sharing Activity
        if(!activityHasExpRelated){//(listExpense==null){
            //TODO: the sharing activity does not have any expense related so delete the user on sharingActivity/users
            if(unicmember) {//only me and no expenses related
                Log.v("Test SAA -----","CASE ONLY ME NO EXP "+infoData.getKey());
                DataManager.getsInstance().deleteSharingActivity(infoData.getKey());
            }else{//many on group but no expenses related
                Log.v("Test SAA -----","CASE MANY USR NO EXP "+infoData.getKey());
                DataManager.getsInstance().leaveSharingActivity(infoData.getKey(),mCurrentUser);

                try{
                    MessagingManager.unsubscribeFromSharingActivity(infoData.getKey());
                }catch(Exception e){
                    e.printStackTrace();
                }

            }

        }else{
            if(unicmember){//only me but many expenses related

                DataManager.getsInstance().deleteExpenseBySharActivity(infoData.getKey());
                DataManager.getsInstance().deleteSharingActivity(infoData.getKey());
                try{
                    MessagingManager.unsubscribeFromSharingActivity(infoData.getKey());
                }catch(Exception e){
                    e.printStackTrace();
                }

            }else{//many on group with many expenses related

                //POP UP THAT SAYS that if you have expenses envolved you can delete it

                AlertDialog.Builder mPopup = new AlertDialog.Builder(((SharingActivitiesListFragment) this.mListener).getActivity());//traer el activity
                mPopup.setIcon(R.drawable.appicon);
                mPopup.setTitle(R.string.lbl_error_can_not_leave_title);
                mPopup.setMessage(R.string.lbl_error_can_not_leave_reason);
                mPopup.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = mPopup.create();
                alertDialog.show();
                ((SharingActivitiesListFragment) this.mListener).getActivity().finish();
                Intent intent = new Intent(((SharingActivitiesListFragment) this.mListener).getActivity(), it.polito.mad.countonme.CountOnMeActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                ((SharingActivitiesListFragment) this.mListener).startActivity(intent);
            }
            //TODO: check if on the listExpese there are any expense related with the user..but it doesnt mean that must be deleted so... more or les do nothing
            //TODO: but if the sharing activity only have one user (current user) then delete all expenses.
        }
        notifyDataSetChanged();
    }

    private boolean checkunicmember(SharingActivity infoData) {
        boolean valuetoReturn= false;

        if(infoData.getUsers().size()!=1){
            valuetoReturn= false;
        }else{
            HashMap<String,User> aux = (HashMap<String, User>) infoData.getUsers();
            Iterator it = aux.keySet().iterator();
            while(it.hasNext()){
               String key = it.next().toString();
               User u =aux.get(key);
                if(u.getId().equals(mCurrentUser)){//mListener.getActivity().getApplication().getCurrentUser().getId()) {
                    valuetoReturn = true;
                }else{
                    valuetoReturn= false;
                }
            }
        }
        return valuetoReturn;
    }
}
