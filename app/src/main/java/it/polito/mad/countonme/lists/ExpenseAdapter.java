package it.polito.mad.countonme.lists;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseBooleanArray;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.polito.mad.countonme.CountOnMeActivity;
import it.polito.mad.countonme.ExpensesListFragment;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.SharingActivitiesListFragment;
import it.polito.mad.countonme.business.ImageManagement;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.ReportBackAction;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.storage.StorageManager;
import it.polito.mad.countonme.swiper.SwipeHelperExpenses;


/**
 * Created by LinaMaria on 5/04/2017.
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpViewHolder> {

    private int counter;

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
            String imgUrl = expense.getImageUrl();

            if( imgUrl != null && imgUrl.length() > 0 ) {
                namePhoto = StorageManager.STORAGE_EXPENSES_FOLDER + "/" + expense.getKey();
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


    private static List<Expense> mExpense;
    private LayoutInflater mInflater;
    public IOnListItemClickListener mListener;
    private int currentPosition;
    private Expense infoData;
    private Context mContext;
    public Activity currentActivity;
    public ExpensesListFragment expensesListFragment;

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

    public ExpenseAdapter(Context context, List<Expense> data, IOnListItemClickListener listener, ExpensesListFragment expensesListFragment ) {
        mExpense = new ArrayList<Expense>(data);
        mListener = listener;
        mInflater = LayoutInflater.from( context );
        this.mContext=context;
        this.expensesListFragment = expensesListFragment;
    }

    @Override
    public ExpenseAdapter.ExpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.expenses_list_item, parent, false);
        return new ExpenseAdapter.ExpViewHolder( view );
    }

    @Override
    public void onBindViewHolder(final ExpenseAdapter.ExpViewHolder holder, final int position) {
        //TODO CHeck mExpense Keys
        Expense e = mExpense.get(position);

        String key= e.getKey();
        if (key == null) {
            Toast.makeText( mInflater.getContext(),"hola", Toast.LENGTH_LONG).show();
        }
        holder.setData( mExpense.get( position ), mListener  );
        infoData = mExpense.get(position);

        holder.mImgView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                infoData = mExpense.get(position);
                Activity parentActivity  = expensesListFragment.getActivity();
                ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_VIEW_EXPENSE_DETAILS, infoData ));
            }
        });
        holder.mTvName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                infoData = mExpense.get(position);
                Activity parentActivity  = expensesListFragment.getActivity();
                ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_VIEW_EXPENSE_DETAILS, infoData ));
            }
        });
        holder.mTvAmount.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                infoData = mExpense.get(position);
                Activity parentActivity  = expensesListFragment.getActivity();
                ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_VIEW_EXPENSE_DETAILS, infoData ));
            }
        });

        holder.mImgView.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                infoData = mExpense.get(position);
                String toshow="";
                if(infoData.getKey()==null){
                    toshow+="key";
                }else if(infoData.getName()==null){
                    toshow+="name";
                }else if(infoData.getDescription()==null){
                    toshow+="desc";
                }
               // String toshow = infoData.getKey()+ " - "+infoData.getName()+ " - "+infoData.getDescription();
                Toast.makeText( mInflater.getContext(),toshow, Toast.LENGTH_LONG).show();
               expensesListFragment.prepareSelection(infoData);
                return false;
            }
        });
        holder.mTvName.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                infoData = mExpense.get(position);
                String toshow = infoData.getKey()+ " - "+infoData.getName()+ " - "+infoData.getDescription();
                Toast.makeText( mInflater.getContext(),toshow, Toast.LENGTH_LONG).show();
                expensesListFragment.prepareSelection(infoData);
                return false;
            }
        });
        holder.mTvAmount.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                infoData = mExpense.get(position);
                String toshow = infoData.getKey()+ " - "+infoData.getName()+ " - "+infoData.getDescription();
                Toast.makeText( mInflater.getContext(),toshow, Toast.LENGTH_LONG).show();
                expensesListFragment.prepareSelection(infoData);
                return false;
            }
        });



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
    public void dimissExpense(int position){
        removeItem(mExpense.get(position));
        if(position==mExpense.size()){
            position--;
        }
        mExpense.remove(position);
        this.notifyItemRemoved(position);
        this.expensesListFragment.setCounter(0);
        this.expensesListFragment.updateCounter(0);
    }

    public void updateAdapter(final ArrayList<Expense> list) {
        this.counter = list.size();
        if(counter!=0) {
            AlertDialog.Builder mPopup = new AlertDialog.Builder(((ExpensesListFragment) this.mListener).getActivity());//traer el activity
            mPopup.setIcon(R.drawable.appicon);
            mPopup.setTitle(R.string.lbl_deleteExpense);
            mPopup.setMessage(R.string.lbl_confirmationdeleteExpense);
            mPopup.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    while (counter != 0) {
                        Iterator<Expense> iter = list.iterator();
                        if (iter.hasNext()) {
                            Expense exp = iter.next();
                            ExpensesListFragment.selection_list.remove(exp);
                            removeItem(exp);
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
}
