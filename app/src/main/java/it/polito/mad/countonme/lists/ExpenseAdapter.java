package it.polito.mad.countonme.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.business.ImageManagement;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.swiper.SwipeHelperExpenses;


/**
 * Created by LinaMaria on 5/04/2017.
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpViewHolder> {

    public static class ExpViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgView;
        TextView mTvName;
        TextView mTvAmount;
        private StorageReference mStorageRef;


        private static NumberFormat mFormatter = new DecimalFormat("#0.00");

        ExpViewHolder(View itemView ) {
            super( itemView );
            mImgView = (ImageView) itemView.findViewById(R.id.expense_img);
            mTvName = (TextView) itemView.findViewById( R.id.expense_name );
            mTvAmount = (TextView) itemView.findViewById( R.id.expense_amount);
        }

        public void setData(final Expense expense, final IOnListItemClickListener listener ) {
            String namePhoto;
            mStorageRef = FirebaseStorage.getInstance().getReference();
            //mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("");
            //mStorageRef.getDownloadUrl().addOnSuccessListener();
//
            String imgUrl = expense.getImageUrl();


            if( imgUrl != null && imgUrl.length() > 0 ) {
                namePhoto = "expenses/" + expense.getKey() + ".jpg";
                StorageReference newstoragereference = mStorageRef.child(namePhoto);

                Glide.with(mImgView.getContext()).using(new ImageManagement()).load(newstoragereference).asBitmap().centerCrop().into(new BitmapImageViewTarget(mImgView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mImgView.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mImgView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }else
                mImgView.setImageResource( R.drawable.img_sharing_default );

            mTvName.setText( expense.getName() );
            mTvAmount.setText( mFormatter.format( expense.getAmount() ) + "" );

            // attach the listener to the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( listener != null )
                        listener.onItemClick( expense );
                }
            });
        }
    }


    private List<Expense> mExpense;
    private LayoutInflater mInflater;
    public IOnListItemClickListener mListener;
    private int currentPosition;
    private Expense infoData;

    public List<Expense> getmExpense() {
        return mExpense;
    }

    public void setmExpense(List<Expense> mExpense) {
        this.mExpense = mExpense;
    }

    public Expense getInfoData() {
        return infoData;
    }

    public void setInfoData(Expense infoData) {
        this.infoData = infoData;
    }

    public ExpenseAdapter(Context context, List<Expense> data, IOnListItemClickListener listener ) {
        mExpense = data;
        mListener = listener;
        mInflater = LayoutInflater.from( context );
    }

    @Override
    public ExpenseAdapter.ExpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.expenses_list_item, parent, false);
        return new ExpenseAdapter.ExpViewHolder( view );
    }

    @Override
    public void onBindViewHolder(final ExpenseAdapter.ExpViewHolder holder, final int position) {
        holder.setData( mExpense.get( position ), mListener  );
        infoData = mExpense.get(position);
        /*
        holder.mImgView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Toast.makeText(holder.mImgView.getContext(), "on click called at position "+position,Toast.LENGTH_SHORT).show();
            }
        });

        holder.mImgView.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                //Toast.makeText(holder.mImgView.getContext(), "on LONG click called at position "+position,Toast.LENGTH_SHORT).show();
                infoData = mExpense.get(position);
                removeItem(infoData);
                return false;
            }
        });
        holder.mTvName.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                //Toast.makeText(holder.mImgView.getContext(), "on LONG click called at position "+position,Toast.LENGTH_SHORT).show();
                infoData = mExpense.get(position);
                removeItem(infoData);
                return false;
            }
        });
        holder.mTvAmount.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                //Toast.makeText(holder.mImgView.getContext(), "on LONG click called at position "+position,Toast.LENGTH_SHORT).show();
                infoData = mExpense.get(position);
                removeItem(infoData);
                return false;
            }
        });
        */


    }

    @Override
    public int getItemCount() {
        return mExpense.size();
    }

    //remove expense from the list (recycler view) also delete from firebase (expense asociated to the current sharing activity, storage image)
    public void removeItem(Expense infoData){
        int currentposition = mExpense.indexOf(infoData);
        DataManager.getsInstance().deleteExpense(infoData.getParentSharingActivityId(),infoData.getKey());
        mExpense.remove(currentposition);
        notifyItemRemoved(currentposition);
    }

    //DISMISS
    public void dimissExense(int position){
        removeItem(mExpense.get(position));
        if(position==mExpense.size()){
            position--;
        }
        mExpense.remove(position);
        this.notifyItemRemoved(position);
    }
}
