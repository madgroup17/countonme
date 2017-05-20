package it.polito.mad.countonme.swiper;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import it.polito.mad.countonme.CountOnMeActivity;
import it.polito.mad.countonme.ExpensesListFragment;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.lists.ExpenseAdapter;
import it.polito.mad.countonme.models.Expense;

/**
 * Created by LinaMaria on 16/05/2017.
 */

public class SwipeHelperExpenses extends ItemTouchHelper.SimpleCallback {

    ExpenseAdapter adapter;
    private Expense infoData;

    public SwipeHelperExpenses(int dragDirs, int swipeDirs){
        super(dragDirs,swipeDirs);
    }
    public SwipeHelperExpenses(ExpenseAdapter adapter){
        super(ItemTouchHelper.DOWN|ItemTouchHelper.UP,ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        //TODO: make appear a pop up for confirmation, if it is ok do de remove, otherwise, do nothing, because will update the recycler view with firebase.
        AlertDialog.Builder mPopup = new AlertDialog.Builder(((ExpensesListFragment) this.adapter.mListener).getActivity());//traer el activity
        mPopup.setIcon(R.drawable.appicon);
        mPopup.setTitle(R.string.lbl_deleteExpense);
        mPopup.setMessage(R.string.lbl_confirmationdeleteExpense);
        mPopup.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.dimissExense(viewHolder.getAdapterPosition());
//              infoData=this.adapter.getmExpense().get(viewHolder.getAdapterPosition());
//              adapter.removeItem(infoData);
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
