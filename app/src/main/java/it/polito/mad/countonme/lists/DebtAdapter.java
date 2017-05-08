package it.polito.mad.countonme.lists;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import it.polito.mad.countonme.R;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.models.DebtValue;

/**
 * Created by Khatereh on 5/7/2017.
 */

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> 
{

    public static class DebtViewHolder extends RecyclerView.ViewHolder 
    {
        TextView mTvDebterName;
        TextView mTvAmount;
        TextView mTvSpendName;        

        private static NumberFormat mFormatter = new DecimalFormat("#0.00");

        DebtViewHolder(View itemView ) {
            super( itemView );
            mTvDebterName = (TextView) itemView.findViewById(R.id.debter_name);
            mTvAmount = (TextView) itemView.findViewById( R.id.owes_amount);
            mTvSpendName = (TextView) itemView.findViewById( R.id.spend_name);           
        }

        public void setData(final DebtValue debt, final IOnListItemClickListener listener ) {

            String Debter = "";
            if(debt.getDebterUser().getName()!=null)
                Debter = debt.getDebterUser().getName();
            else
                Debter = debt.getDebterUser().getEmail();

            String Spend = "";
            if(debt.getUser().getName()!=null)
                Spend = debt.getUser().getName();
            else
                Spend = debt.getUser().getEmail();

            mTvDebterName.setText(Debter);
            mTvSpendName.setText(Spend);
            mTvAmount.setText( mFormatter.format( debt.getAmount() ) + "" );

            // attach the listener to the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( listener != null )
                        listener.onItemClick(debt);
                }
            });
        }
    }


    private List<DebtValue> mdebt;
    private LayoutInflater mInflater;
    private IOnListItemClickListener mListener;

    public DebtAdapter(Context context, List<DebtValue> data, IOnListItemClickListener listener ) {
        mdebt = data;
        mListener = listener;
        mInflater = LayoutInflater.from( context );
    }

    @Override
    public DebtAdapter.DebtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.debt_item, parent, false);
        return new DebtAdapter.DebtViewHolder( view );
    }

    @Override
    public void onBindViewHolder(DebtAdapter.DebtViewHolder holder, int position) {
        holder.setData( mdebt.get( position ), mListener  );
    }

    @Override
    public int getItemCount() {
        return mdebt.size();
    }

}
