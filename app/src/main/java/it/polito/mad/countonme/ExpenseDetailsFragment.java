package it.polito.mad.countonme;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.polito.mad.countonme.database.ExpenseLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;

public class ExpenseDetailsFragment extends BaseFragment implements IOnDataListener {

    @BindView( R.id.iv_exp_img) ImageView mIvPhoto;
    @BindView( R.id.tv_created_by ) TextView mTvCreatedBy;
    @BindView( R.id.tv_name ) TextView mTvName;
    @BindView( R.id.tv_description ) TextView mTvDescription;
    @BindView( R.id.tv_currency ) TextView mTvCurrency;
    @BindView( R.id.tv_amount ) TextView mTvAmount;
    @BindView( R.id.tv_date ) TextView mTvDate;
    @BindView( R.id.tv_money_transfer ) TextView mTvMoneyTransfer;
    @BindView( R.id.tv_shared_evenly ) TextView mTvSharedEvenly;

    @BindView( R.id.ll_sharing_info )  LinearLayout mLlShareInfo;

    private Unbinder mUnbinder;

    private ExpenseLoader mExpenseLoader;

    @Override
    public void onAttach(Context context) { super.onAttach(context); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_details_fragment, container, false);
        mUnbinder = ButterKnife.bind( this, view );

        if (savedInstanceState != null) {
            setData( AppConstants.SHARING_ACTIVITY_KEY, savedInstanceState.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
            setData( AppConstants.EXPENSE_KEY, savedInstanceState.getString( AppConstants.EXPENSE_KEY ) );
        }

        mExpenseLoader = new ExpenseLoader();
        mExpenseLoader.setOnDataListener( this );

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString( AppConstants.SHARING_ACTIVITY_KEY, (String) getData( AppConstants.SHARING_ACTIVITY_KEY ) );
        outState.putString( AppConstants.EXPENSE_KEY, (String) getData( AppConstants.EXPENSE_KEY ) );
    }


    @Override
    public void onData( Object data ) {
        if( data instanceof Expense) {
            fillUi( ( Expense ) data );
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        try {
            mExpenseLoader.loadExpense( ( String ) getData( AppConstants.SHARING_ACTIVITY_KEY ),
                    ( String ) getData( AppConstants.EXPENSE_KEY ) );
        } catch (DataLoaderException e) {
            e.printStackTrace();
        }
    }


    private void adjustActionBar()
    {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_details_title );
        setHasOptionsMenu(true);
    }


    private void fillUi( Expense expense ) {
        if( expense == null ) return;

        mTvCreatedBy.setText( String.format( getResources().getString(R.string.lbl_created_by ), expense.getCreatedBy().getName() ) );
        /*mTvName;
        mTvDescription;
        mTvCurrency;
        mTvAmount;
        mTvDate;
        mTvMoneyTransfer;
        mTvSharedEvenly;*/




    /*    mTvName.setText( activity.getName() );
        Resources res = getResources();
        String createdBy = String.format( res.getString(R.string.lbl_created_by ), activity.getCreatedBy().getName() );
        mTvCreatedBy.setText( createdBy );
        mTvDescription.setText( activity.getDescription() );
        mTvCurrency.setText( activity.getCurrency() );

        LayoutInflater myInflater = LayoutInflater.from( getActivity() );

        mLlUsers.removeAllViews();

        for (Map.Entry<String, User> entry : activity.getUsers().entrySet()) {
            User user = entry.getValue();
            View child = myInflater.inflate( R.layout.user_list_item, null );
            ImageView userPhoto = ( ImageView ) child.findViewById( R.id.user_img );
            TextView userName = (TextView) child.findViewById( R.id.user_name );
            TextView userEmail = ( TextView ) child.findViewById( R.id.user_email );
            //new ImageFromUrlTask( userPhoto, R.drawable.default_user_photo, true ).execute( user.getPhotoUrl() );
            userName.setText( user.getName() );
            userEmail.setText( user.getEmail() );

            mLlUsers.addView( child );
        }*/
    }

}
