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
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;
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
    public IOnListItemClickListener mListener;
    private String mCurrentUser;
    private ExpensesListLoader mExpensesListLoader;
    //private SharingActivitiesListFragment sharingActivitiesListFragment;

    public SharingActivitiesAdapter(Context context, List<SharingActivity> data, IOnListItemClickListener listener,String currentUser ) {
        mSharingActivities = data;
        mListener = listener;
        mInflater = LayoutInflater.from( context );
        mCurrentUser=currentUser;
        // sharingActivitiesListFragment = new SharingActivitiesListFragment();
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

    public void dimissSharingActivity(int position) {
        removeItemShAct(mSharingActivities.get(position));
        if(position==mSharingActivities.size()){
            position--;
        }
        mSharingActivities.remove(position);
        this.notifyItemRemoved(position);
        Toast.makeText( mInflater.getContext(), "entro a dimissSharingActivity", Toast.LENGTH_LONG).show();
    }

    private void removeItemShAct(SharingActivity infoData) {
        boolean unicmember = checkunicmember(infoData);//if true means unique user on sharing activity
        DatabaseReference dbRef = DataManager.getsInstance().getSharActExpensesReference(infoData.getKey());
        boolean activityHasExpRelated =false;
        activityHasExpRelated=DataManager.checkExpRelofShaAct(infoData.getKey());
        //ArrayList<Expense> listExpense = new ArrayList<>();
        //listExpense=DataManager.checkExpensesRelatedOfSharingActivity(infoData.getKey());
//TODO: check if exist any expense related to this Sharing Activity
        if(!activityHasExpRelated){//(listExpense==null){
            //TODO: the sharing activity does not have any expense related so delete the user on sharingActivity/users
            if(unicmember) {
                DataManager.getsInstance().deleteSharingActivity(infoData.getKey());
            }else{
                DataManager.getsInstance().leaveSharingActivity(infoData.getKey(),mCurrentUser);
            }

        }else{
            if(unicmember){
                //for(Expense exp :listExpense){
                  DataManager.getsInstance().deleteExpenseBySharActivity(infoData.getKey());
                  DataManager.getsInstance().deleteSharingActivity(infoData.getKey());
                //    listExpense.remove(exp);
                //}
            }else{
                DataManager.getsInstance().leaveSharingActivity(infoData.getKey(),mCurrentUser);
            }
            //TODO: check if on the listExpese there are any expense related with the user..but it doesnt mean that must be deleted so... more or les do nothing
            //TODO: but if the sharing activity only have one user (current user) then delete all expenses.
        }
        Toast.makeText( mInflater.getContext(), "entro a removeItem", Toast.LENGTH_LONG).show();
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
                //((CountOnMeApp)getActivity().getApplication()).getCurrentUser().getId();
                if(u.getId().equals(mCurrentUser)){//mListener.getActivity().getApplication().getCurrentUser().getId()) {
                    valuetoReturn = true;
                }else{
                    valuetoReturn= false;
                }
            }

             /*
                    HashMap<String,String> aux2 = aux.get(key);
                    Iterator it2 = aux2.keySet().iterator();
                    while(it2.hasNext()){
                        String key2 = it2.next().toString();
                        if(key2.equals("id")){
                            idUser = aux2.get(key2);
                            Log.d("id of user: ",idUser);
                        }else if(key2.equals("name")){
                            nUser = aux2.get(key2);
                            Log.d("name of user:",nUser);
                        }else if(key2.equals("email")){
                            emailUser = aux2.get(key2);
                            Log.d("name of user:",emailUser);
                        }else if(key2.equals("url")){
                            urlUser = aux2.get(key2);
                            Log.d("name of user:",urlUser);
                        }

                    }
                    User userToAdd = new User(idUser,nUser,emailUser,urlUser);
                    UserMap = new HashMap<String,User> ();
                    UserMap.put(idUser,userToAdd);
                }
             * */

        }
        return valuetoReturn;
    }
    /*
      public void removeItem(Expense infoData){
        int currentposition = mExpense.indexOf(infoData);
        DataManager.getsInstance().deleteExpense(infoData.getParentSharingActivityId(),infoData.getKey());
        mExpense.remove(currentposition);
        notifyItemRemoved(currentposition);
    }

    * */

}
