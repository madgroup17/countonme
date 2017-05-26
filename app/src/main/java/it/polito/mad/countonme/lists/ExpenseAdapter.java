package it.polito.mad.countonme.lists;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
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
        View mItemView ;
        ImageView mImgView;
        TextView mTvName;
        TextView mTvAmount;
        private StorageReference mStorageRef;

        private static NumberFormat mFormatter = new DecimalFormat("#0.00");

        ExpViewHolder(View itemView ) {
            super( itemView );
            mItemView=itemView;
            mImgView = (ImageView) itemView.findViewById(R.id.expense_img);
            mTvName = (TextView) itemView.findViewById( R.id.expense_name );
            mTvAmount = (TextView) itemView.findViewById( R.id.expense_amount);
        }

        public void setData(final Expense expense, final IOnListItemClickListener listener ) {
            String namePhoto;
            mStorageRef = FirebaseStorage.getInstance().getReference();
            String imgUrl = expense.getImageUrl();
            if(ExpenseAdapter.selection_list.size()!=0){
                for(Expense expaux : ExpenseAdapter.selection_list){
                    if(expaux.getKey().equals(expense.getKey())){
                        if(!mItemView.isSelected()){
                            mItemView.setSelected(true);
                            int color = Color.parseColor("#AAAAAAAA");
                            int colorWHITE = Color.parseColor("#FFFFFF");
                            mItemView.setBackgroundColor(color);
                            mImgView.setBackgroundColor(color);
                            mItemView.setBackgroundColor(colorWHITE);
                            mTvName.setBackgroundColor(colorWHITE);
                            mTvAmount.setBackgroundColor(colorWHITE);
                        }
                    }else{
                        if(mItemView.isSelected()){
                            mItemView.setSelected(false);
                            int color = Color.parseColor("#FFFFFF");
                            mImgView.setBackgroundColor(color);
                            mItemView.setBackgroundColor(color);
                        }
                    }
                }
            }

            if( imgUrl != null && imgUrl.length() > 0 ) {
                Glide.with(mImgView.getContext()).load(Uri.parse(imgUrl)).into( mImgView );
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
    public static ArrayList<Expense> selection_list;

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

    public ExpenseAdapter(Context context, List<Expense> data, IOnListItemClickListener listener, ExpensesListFragment expensesListFragment ,ArrayList<Expense> selection_list) {
        mExpense = new ArrayList<Expense>(data);
        mListener = listener;
        mInflater = LayoutInflater.from( context );
        this.mContext=context;
        this.expensesListFragment = expensesListFragment;
        this.selection_list=selection_list;
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

        holder.mItemView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                infoData = mExpense.get(position);
                Activity parentActivity  = expensesListFragment.getActivity();
                ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_VIEW_EXPENSE_DETAILS, infoData ));
            }
        });

        holder.mItemView.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                infoData = mExpense.get(position);
                if(v.isSelected()){
                    v.setSelected(false);
                    int color = Color.parseColor("#FFFFFF");
                    holder.mImgView.setBackgroundColor(color);
                    holder.mItemView.setBackgroundColor(color);
                }else{
                    v.setSelected(true);
                    int color = Color.parseColor("#AAAAAAAA");
                    int colorWHITE = Color.parseColor("#FFFFFF");
                    v.setBackgroundColor(color);
                    holder.mImgView.setBackgroundColor(color);
                    holder.mItemView.setBackgroundColor(colorWHITE);
                    holder.mTvName.setBackgroundColor(colorWHITE);
                    holder.mTvAmount.setBackgroundColor(colorWHITE);
                }
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
