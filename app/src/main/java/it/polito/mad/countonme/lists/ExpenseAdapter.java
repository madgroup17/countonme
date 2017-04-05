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
import it.polito.mad.countonme.interfaces.OnListItemClickListener;
import it.polito.mad.countonme.models.Expense;


/**
 * Created by LinaMaria on 5/04/2017.
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpViewHolder> {

    public static class ExpViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgView;
        TextView mTextView;

        ExpViewHolder(View itemView ) {
            super( itemView );
            mImgView = (ImageView) itemView.findViewById(R.id.expense_img);
            mTextView = (TextView) itemView.findViewById( R.id.expense_name );
        }

        public void setData(final Expense expense, final OnListItemClickListener listener ) {
            String imgUrl = expense.getImageUrl();

            if( imgUrl != null && imgUrl.length() > 0 )
                mImgView.setImageURI( Uri.parse( imgUrl ) );
            else
                mImgView.setImageResource(R.drawable.img_sharing_default);

            mTextView.setText( expense.getName() );

            // attach the listener to the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick( expense );
                }
            });
        }
    }


    private List<Expense> mExpense;
    private LayoutInflater mInflater;
    private OnListItemClickListener mListener;

    public ExpenseAdapter(Context context, List<Expense> data, OnListItemClickListener listener ) {
        mExpense = data;
        mListener = listener;
        mInflater = LayoutInflater.from( context );
    }

    @Override
    public ExpenseAdapter.ExpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.expense_item_list, parent, false);
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
