package it.polito.mad.countonme.swiper;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.SharingActivitiesListFragment;
import it.polito.mad.countonme.lists.SharingActivitiesAdapter;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Created by LinaMaria on 22/05/2017.
 */

public class SwipeHelperSharingActivities extends ItemTouchHelper.SimpleCallback {
    SharingActivitiesAdapter adapter;
    private SharingActivity infoData;

    public SwipeHelperSharingActivities(int dragDirs, int swipeDirs){
        super(dragDirs,swipeDirs);
    }
    public SwipeHelperSharingActivities(SharingActivitiesAdapter adapter){
        super(ItemTouchHelper.DOWN|ItemTouchHelper.UP,ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        AlertDialog.Builder mPopup = new AlertDialog.Builder(((SharingActivitiesListFragment) this.adapter.mListener).getActivity());//traer el activity
        mPopup.setIcon(R.drawable.appicon);
        mPopup.setTitle(R.string.lbl_deleteSharingActivity);
        mPopup.setMessage(R.string.lbl_confirmationdeleteSharingActivity);
        mPopup.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.dimissSharingActivity(viewHolder.getAdapterPosition());
                dialog.dismiss();
            }
        });
        mPopup.setNegativeButton(R.string.lbl_no, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = mPopup.create();
        alertDialog.show();
    }
}
