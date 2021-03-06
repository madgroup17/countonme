package it.polito.mad.countonme;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.polito.mad.countonme.business.CurrencyManagment;
import it.polito.mad.countonme.business.LinkSharing;
import it.polito.mad.countonme.database.SharingActivityLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;

/**
 * Created by Khatereh on 4/28/2017.
 */

public class SharingActivityDetailsFragment extends BaseFragment implements IOnDataListener {

    @BindView(R.id.iv_shact_img) ImageView mIvPhoto;
    @BindView(R.id.tv_name) TextView mTvName;
    @BindView(R.id.tv_created_by) TextView mTvCreatedBy;
    @BindView(R.id.tv_description) TextView mTvDescription;
    @BindView(R.id.tv_currency) TextView mTvCurrency;

    @BindView(R.id.ll_users) LinearLayout mLlUsers;

    private Unbinder mUnbinder;


    private SharingActivityLoader mSharingActivityLoader;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sharing_activity_details_fragment, container, false);
        mUnbinder = ButterKnife.bind( this, view );

        if (savedInstanceState != null)
            setData( savedInstanceState.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
        else {
            Bundle args = getArguments();
            if (args != null) setData(args.getString(AppConstants.SHARING_ACTIVITY_KEY));
        }

        mSharingActivityLoader = new SharingActivityLoader();
        mSharingActivityLoader.setOnDataListener( this );

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString( AppConstants.SHARING_ACTIVITY_KEY, (String) getData());
    }

    @Override
    public void onData( Object data ) {
        if( data instanceof SharingActivity) {
            fillUi( ( SharingActivity ) data );
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
            mSharingActivityLoader.loadSharingActivity( (String) getData() );
        } catch (DataLoaderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //setHasOptionsMenu(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_sharing_activity:
                try {
                    Intent sendIntent = LinkSharing.shareActivity(getActivity(), (String) getData());
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.select_app)));
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.lbl_error_sharing_link), Toast.LENGTH_SHORT).show();
                }
                return true;
           /* case R.id.edit_sharing_activity:
                Activity parentActivity  = getActivity();
                if( parentActivity instanceof IActionReportBack) {
                    ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_EDIT_SHARING_ACTIVITY, getData() ) );
                }
                return true;
            case R.id.delete_sharing_activity:
                Toast.makeText(getActivity(), getResources().getString( R.string.temp_not_implemeted_lbl ), Toast.LENGTH_SHORT).show();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sharing_activity_details_menu, menu);
    }

    private void adjustActionBar()
    {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.sharing_activity_details_title );
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fillUi(SharingActivity activity ) {
        if( activity == null ) return;
        try {
            mTvName.setText(activity.getName());
            Resources res = getResources();
            String createdBy = String.format(res.getString(R.string.lbl_created_by), activity.getCreatedBy().getName());
            mTvCreatedBy.setText(createdBy);
            mTvDescription.setText(activity.getDescription());
            mTvCurrency.setText( CurrencyManagment.GetText(Integer.valueOf(activity.getCurrency()),getActivity() ));
            if( activity.getImageUrl() != null )
                Glide.with( mIvPhoto.getContext()).load( activity.getImageUrl() ).into( mIvPhoto );


            LayoutInflater myInflater = LayoutInflater.from(getActivity());

            mLlUsers.removeAllViews();

            for (Map.Entry<String, User> entry : activity.getUsers().entrySet()) {
                User user = entry.getValue();
                View child = myInflater.inflate(R.layout.user_list_item, null);
                ImageView userPhoto = (ImageView) child.findViewById(R.id.user_img);
                TextView userName = (TextView) child.findViewById(R.id.user_name);
                TextView userEmail = (TextView) child.findViewById(R.id.user_email);
                if( user.getPhotoUrl() != null )
                    Glide.with( userPhoto.getContext()).load( user.getPhotoUrl() ).into( userPhoto );
                else
                    userPhoto.setImageResource(R.drawable.default_user_photo );
                userName.setText(user.getName());
                userEmail.setText(user.getEmail());

                mLlUsers.addView(child);
            }
        } catch(Exception ex ) {
            // ignore
        }
    }


}
