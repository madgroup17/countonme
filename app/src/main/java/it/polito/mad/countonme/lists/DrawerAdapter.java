package it.polito.mad.countonme.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.interfaces.IOnDrawerItemListener;
import it.polito.mad.countonme.models.DrawerItem;

/**
 * Created by francescobruno on 22/04/17.
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerItemViewHolder> {

    public static class DrawerItemViewHolder extends RecyclerView.ViewHolder {
        @BindView( R.id.drawer_item_icon) ImageView mIcon;
        @BindView( R.id.drawer_item_label) TextView mLabel;

        DrawerItemViewHolder( View itemView ) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }

        public void setData( final DrawerItem item, final IOnDrawerItemListener listener ) {
            mIcon.setImageResource( item.getItemIconResId() );
            mLabel.setText( item.getItemLabelResId() );

            // attach the listener to the view
            itemView.setOnClickListener(  new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    if ( listener != null )
                        listener.onDrawerItemClick( item.getItemId() );
                }
            });
        }
    }

    private List<DrawerItem> mDrawerItemsList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private IOnDrawerItemListener mListener;

    public DrawerAdapter(Context context, List<DrawerItem> items, IOnDrawerItemListener listener ) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from( mContext );
        mDrawerItemsList = items;
        mListener = listener;
    }

    @Override
    public DrawerItemViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = mLayoutInflater.inflate( R.layout.drawer_item_layout, parent, false );
        return new DrawerItemViewHolder( view );
    }

    @Override
    public void onBindViewHolder(DrawerItemViewHolder holder, int position) {
        holder.setData( mDrawerItemsList.get( position ), mListener );
    }

    @Override
    public int getItemCount() { return mDrawerItemsList.size(); }
}
