package it.polito.mad.countonme.data;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.countonme.R;
import it.polito.mad.countonme.models.DrawerItem;

/**
 * Created by francescobruno on 22/04/17.
 */

public class NavigationDrawerData {

    private final static int NAV_ITEM_NUM = 2;

    private static int mIds[] = {
            R.id.menu_settings,
            R.id.menu_logout
    };

    private static int mIcons[] = {
        R.drawable.settings,
        R.drawable.logout
    };

    private static int mLabels[] = {
        R.string.lbl_nav_drawer_settings,
        R.string.lbl_nav_drawer_logout
    };

    public static List<DrawerItem> getNavDrawerItemList() {
        List<DrawerItem> itemList = new ArrayList<DrawerItem>();

        for( int i = 0; i < NAV_ITEM_NUM; i ++ )
            itemList.add( new DrawerItem( mIds[ i ], mIcons[ i ], mLabels[ i ] ) );

        return itemList;
    }
}
