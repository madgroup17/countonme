package it.polito.mad.countonme;

import android.app.Fragment;

/**
 * The base fragment with common data information
 * Created by francescobruno on 06/04/17.
 */

public class BaseFragment extends Fragment {

    private Object mData;

    public void setData( Object data ) {
        mData = data;
    }

    public Object getData() {
        return mData;
    }

}
