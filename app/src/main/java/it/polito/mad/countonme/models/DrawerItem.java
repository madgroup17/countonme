package it.polito.mad.countonme.models;

/**
 * Created by francescobruno on 22/04/17.
 */

public class DrawerItem {
    private int mItemId;
    private int mItemIconResId;
    private int mItemLabelResId;

    public DrawerItem() {
        this( -1, 0, 0 );
    }

    public DrawerItem( int itemId, int iconResId, int labelResId ) {
        mItemId         = itemId;
        mItemIconResId  = iconResId;
        mItemLabelResId = labelResId;
    }

    public int getItemId() {
        return mItemId;
    }

    public void setItemId(int itemId) {
        this.mItemId = itemId;
    }

    public int getItemIconResId() {
        return mItemIconResId;
    }

    public void setItemIconResId(int iconResId) {
        this.mItemIconResId = iconResId;
    }

    public int getItemLabelResId() {
        return mItemLabelResId;
    }

    public void setItemLabelResId( int labelResId ) {
        mItemLabelResId = labelResId;
    }
}
