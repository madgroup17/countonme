package it.polito.mad.countonme.swiper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

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
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.dimissExense(viewHolder.getAdapterPosition());
//        infoData=this.adapter.getmExpense().get(viewHolder.getAdapterPosition());
//        adapter.removeItem(infoData);
    }
}
