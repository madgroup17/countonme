package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.polito.mad.countonme.database.ExpenseLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.ReportBackAction;
import it.polito.mad.countonme.models.Share;

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

    @BindView( R.id.ll_sharing_section) LinearLayout mLlSharingSection;
    @BindView( R.id.ll_sharing_info )  LinearLayout mLlShareInfo;

    private Unbinder mUnbinder;

    private ExpenseLoader mExpenseLoader;

    private Expense model;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_details_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            setData(AppConstants.SHARING_ACTIVITY_KEY, savedInstanceState.getString(AppConstants.SHARING_ACTIVITY_KEY));
            setData(AppConstants.EXPENSE_KEY, savedInstanceState.getString(AppConstants.EXPENSE_KEY));
            setData(AppConstants.FROM_NOTIFICATION, savedInstanceState.getBoolean(AppConstants.FROM_NOTIFICATION) );
        }

        mExpenseLoader = new ExpenseLoader();
        mExpenseLoader.setOnDataListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, (String) getData(AppConstants.SHARING_ACTIVITY_KEY));
        outState.putString(AppConstants.EXPENSE_KEY, (String) getData(AppConstants.EXPENSE_KEY));
        outState.putBoolean( AppConstants.FROM_NOTIFICATION, (Boolean) getData(AppConstants.FROM_NOTIFICATION) );
    }


    @Override
    public void onData(Object data) {
        if (data instanceof Expense) {
            model = (Expense) data;
            fillUi(model);
        }
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity()).hideLoadingDialog();
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
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity()).showLoadingDialog();
        try {
            mExpenseLoader.loadExpense((String) getData(AppConstants.SHARING_ACTIVITY_KEY),
                    (String) getData(AppConstants.EXPENSE_KEY));
        } catch (DataLoaderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity()).hideLoadingDialog();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.expense_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_expense:
               Activity parentActivity = getActivity();
                if (parentActivity instanceof IActionReportBack) {
                    Bundle bundle = new Bundle();
                    bundle.putString( AppConstants.SHARING_ACTIVITY_KEY, model.getParentSharingActivityId());
                    bundle.putString(AppConstants.EXPENSE_KEY,model.getKey());
                    bundle.putString(AppConstants.MODE,AppConstants.EDIT_MODE);
                    ((IActionReportBack) parentActivity).onAction(new ReportBackAction(ReportBackAction.ActionEnum.ACTION_EDIT_EXPENSE, bundle));
                }
                return true;
            /*case R.id.delete_sharing_activity:
                Toast.makeText(getActivity(), getResources().getString( R.string.temp_not_implemeted_lbl ), Toast.LENGTH_SHORT).show();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void adjustActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.expense_details_title);
        Object notification = getData( AppConstants.FROM_NOTIFICATION );
        setHasOptionsMenu( notification == null || !((Boolean)notification) );
    }


    private void fillUi(Expense expense) {
        if (expense == null) return;

        NumberFormat formatter = new DecimalFormat("#0.00");
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.fmt_date));

        Glide.with(mIvPhoto.getContext()).load(expense.getImageUrl()).into(mIvPhoto);
        mTvCreatedBy.setText(String.format(getResources().getString(R.string.lbl_created_by), expense.getCreatedBy().getName()));
        mTvName.setText(expense.getName());
        mTvDescription.setText(expense.getDescription());
        mTvCurrency.setText(expense.getExpenseCurrency());
        mTvAmount.setText(formatter.format(expense.getAmount()));
        mTvDate.setText(dateFormat.format(expense.getDate()));
        mTvMoneyTransfer.setText(expense.getIsMoneyTransfer() ? R.string.lbl_yes : R.string.lbl_no);
        mTvSharedEvenly.setText(expense.getIsSharedEvenly() ? R.string.lbl_yes : R.string.lbl_no);

        mLlShareInfo.removeAllViews();
        if (expense.getIsSharedEvenly() == false) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (Map.Entry<String, Share> entry : expense.getShares().entrySet()) {
                Share share = entry.getValue();
                // set the views for expenses sharing
                View child = inflater.inflate(R.layout.share_details_item, null);
                ImageView userPhoto = (ImageView) child.findViewById(R.id.iv_user);
                TextView userName = (TextView) child.findViewById(R.id.tv_name);
                TextView amount = (TextView) child.findViewById(R.id.tv_amount);
                if( share.getUser().getPhotoUrl() != null )
                    Glide.with( userPhoto.getContext()).load( share.getUser().getPhotoUrl() ).into( userPhoto );
                else
                    userPhoto.setImageResource( R.drawable.default_user_photo );
                userName.setText(share.getUser().getName());
                amount.setText(formatter.format(share.getAmount()));

                mLlShareInfo.addView(child);
            }
        }
        mLlSharingSection.setVisibility( expense.getIsSharedEvenly() ? View.GONE : View.VISIBLE );

    }

}
