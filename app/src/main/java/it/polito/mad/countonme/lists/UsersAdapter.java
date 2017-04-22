package it.polito.mad.countonme.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.models.User;

/**
 * Created by francescobruno on 21/04/17.
 */

public class UsersAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<User> mUsers;
    LayoutInflater mInflter;

    public UsersAdapter(Context context, ArrayList<User> users ) {
        mContext = context;
        mUsers = users;
        mInflter = ( LayoutInflater.from( mContext ) );
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if( view != null )
            holder = (ViewHolder) view.getTag();
        else {
            view = mInflter.inflate( R.layout.user_list_item, viewGroup, false );
            holder = new ViewHolder( view );
            view.setTag( holder  );
        }

        User user = mUsers.get( i );
        holder.name.setText( user.getName() );
        return view;
    }

    // VIEW HOLDER CLASS

    static class ViewHolder {
        @BindView( R.id.user_img ) ImageView image;
        @BindView( R.id.user_name ) TextView name;

        public ViewHolder( View view ) {
            ButterKnife.bind( this, view );
        }
    }

}
