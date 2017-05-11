package it.polito.mad.countonme;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.polito.mad.countonme.database.ExpenseLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.Share;
import it.polito.mad.countonme.networking.ImageFromUrlTask;

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
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).hideLoadingDialog();
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
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).showLoadingDialog();
        try {
            mExpenseLoader.loadExpense( ( String ) getData( AppConstants.SHARING_ACTIVITY_KEY ),
                    ( String ) getData( AppConstants.EXPENSE_KEY ) );
        } catch (DataLoaderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).hideLoadingDialog();
    }

    private void adjustActionBar()
    {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_details_title );
        setHasOptionsMenu(true);
    }


    private void fillUi( Expense expense ) {
        if( expense == null ) return;

        NumberFormat formatter = new DecimalFormat("#0.00");
        DateFormat dateFormat = new SimpleDateFormat( getString( R.string.fmt_date ) );

        mTvCreatedBy.setText( String.format( getResources().getString(R.string.lbl_created_by ), expense.getCreatedBy().getName() ) );
        mTvName.setText( expense.getName() );
        mTvDescription.setText( expense.getDescription() );
        mTvCurrency.setText( expense.getExpenseCurrency() );
        mTvAmount.setText( formatter.format( expense.getAmount() ) );
        mTvDate.setText( dateFormat.format( expense.getDate() ) );
        mTvMoneyTransfer.setText( expense.getIsMoneyTransfer() ? R.string.lbl_yes : R.string.lbl_no );
        mTvSharedEvenly.setText( expense.getIsSharedEvenly() ? R.string.lbl_yes : R.string.lbl_no );

        mLlShareInfo.removeAllViews();
        if( expense.getIsSharedEvenly() == false ) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            for (Map.Entry<String, Share> entry : expense.getShares().entrySet()) {
                Share share = entry.getValue();
                // set the views for expenses sharing
                View child = inflater.inflate(R.layout.share_details_item, null);
                ImageView userPhoto = (ImageView) child.findViewById(R.id.iv_user);
                TextView userName = (TextView) child.findViewById(R.id.tv_name);
                TextView amount = (TextView) child.findViewById(R.id.tv_amount);
                new ImageFromUrlTask(userPhoto, R.drawable.default_user_photo, true).execute(share.getUser().getPhotoUrl());
                userName.setText(share.getUser().getName());
                amount.setText(formatter.format(share.getAmount()));

                mLlShareInfo.addView(child);

            }
        }
        mLlShareInfo.setVisibility( expense.getIsSharedEvenly() ? View.GONE : View.VISIBLE );

    }

}
