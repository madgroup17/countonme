package it.polito.mad.countonme;

import android.app.Fragment;

import java.util.HashMap;

/**
 * The base fragment with common data information
 * Created by francescobruno on 06/04/17.
 */

public class BaseFragment extends Fragment {

    private Object mData;
    private HashMap<String, Object>  mDatas = new HashMap<String, Object>();

    public void setData( Object data ) {
        mData = data;
    }
    public Object getData() {
        return mData;
    }

    public void setData( String key, Object data ) {
        mDatas.put( key, data );
    }

    public Object getData( String key ) {
        return mDatas.get( key );
    }

    public void clearDatas() {
        mDatas.clear();
    }


}
