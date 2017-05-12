package it.polito.mad.countonme.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.models.Expense;


/**
 * Created by LinaMaria on 5/04/2017.
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpViewHolder> {

    public static class ExpViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgView;
        TextView mTvName;
        TextView mTvAmount;

        private static NumberFormat mFormatter = new DecimalFormat("#0.00");

        ExpViewHolder(View itemView ) {
            super( itemView );
            mImgView = (ImageView) itemView.findViewById(R.id.expense_img);
            mTvName = (TextView) itemView.findViewById( R.id.expense_name );
            mTvAmount = (TextView) itemView.findViewById( R.id.expense_amount);
        }

        public void setData(final Expense expense, final IOnListItemClickListener listener ) {
            String imgUrl = expense.getImageUrl();

            if( imgUrl != null && imgUrl.length() > 0 )
                Glide.with( mImgView.getContext()).load( expense.getImageUrl() ).asBitmap().centerCrop().into(new BitmapImageViewTarget(mImgView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create( mImgView.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mImgView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            else
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
    private IOnListItemClickListener mListener;

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
    public void onBindViewHolder(ExpenseAdapter.ExpViewHolder holder, int position) {
        holder.setData( mExpense.get( position ), mListener  );
    }

    @Override
    public int getItemCount() {
        return mExpense.size();
    }

}
