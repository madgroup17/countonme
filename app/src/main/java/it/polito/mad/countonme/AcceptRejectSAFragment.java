package it.polito.mad.countonme;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.models.ReportBackAction;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;

/**
 * Created by LinaMaria on 10/05/17.
 */


public class AcceptRejectSAFragment extends BaseFragment implements ValueEventListener {

    private FragmentManager mFragmentManager;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private TextView mNameSADetails;
    private TextView mCreatedBy;
    private TextView mDetailsSADetails;
    private TextView mCurrencySADetails;
    private ImageView mIvPhoto;
    private boolean vacio;
    HashMap<String,User> UserMap;
    String nUser;
    String idUser;
    String emailUser;
    String urlUser;
    private String path;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args;
        if (savedInstanceState != null)
            setData(savedInstanceState.getString(AppConstants.SHARING_ACTIVITY_KEY));
        else {
            args = getArguments();
            if (args != null) setData(args.getString(AppConstants.SHARING_ACTIVITY_KEY));
        }
        View view = inflater.inflate(R.layout.accept_reject_sa, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getActivity().getIntent();
        path = intent.getData().getPath();
        path = path.substring(1);
        DataManager.getsInstance().getSharingActivityReference(path).addListenerForSingleValueEvent(this);

        mNameSADetails = (TextView) view.findViewById(R.id.tv_name);
        mCreatedBy = (TextView) view.findViewById( R.id.tv_created_by );
        mDetailsSADetails = (TextView) view.findViewById(R.id.tv_description);
        mCurrencySADetails = (TextView) view.findViewById(R.id.tv_currency);
        mIvPhoto = (ImageView) view.findViewById( R.id.iv_shact_img );

        SharingActivity model = new SharingActivity();

        final ImageView buttonAccept = (ImageView) view.findViewById(R.id.iv_accept);

        buttonAccept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Perform action on click
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.usernologged), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    // the user is logged in so we will show the application first screen
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
                    DatabaseReference actuserref = ref.child("shareacts").child(path).child("users");
                    User userapplevel = ((CountOnMeApp)getActivity().getApplication()).getCurrentUser();
                    String id=userapplevel.getId();
                    String correo =userapplevel.getEmail();
                    String nombre = userapplevel.getName();
                    String urlphoto= userapplevel.getPhotoUrl();

                    User current = new User(id,nombre,correo,urlphoto);//(idUser,nUser,emailUser,urlUser);
                    actuserref.child(id).setValue(current);

                    //redirect to sharing activity item list
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), it.polito.mad.countonme.CountOnMeActivity.class));
                }
            }
        });


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    getActivity().finish();
                    return true;
                } else {
                    return false;
                }
            }
        });

        return view;
    }

    @Override
    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        super.setEnterSharedElementCallback(callback);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, (String) getData());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.invitation_title );

        DataManager.getsInstance().getSharingActivityReference(( String ) getData()).addValueEventListener( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.getsInstance().getSharingActivityReference( ( String ) getData() ).removeEventListener( this );
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Here the data arrives
        //Log.d("Hello", "ello"); // here are your data as you can see
        if(dataSnapshot.getValue() != null){
        SharingActivity myActivity = (SharingActivity) dataSnapshot.getValue(SharingActivity.class);
        mNameSADetails.setText(myActivity.getName());
        mDetailsSADetails.setText(myActivity.getDescription());
        mCurrencySADetails.setText(myActivity.getCurrency());
        String createdBy = String.format(getResources().getString(R.string.lbl_created_by), myActivity.getCreatedBy().getName());
        mCreatedBy.setText(createdBy);
        if( myActivity.getImageUrl() != null )
            Glide.with( mIvPhoto.getContext()).load( myActivity.getImageUrl() ).into( mIvPhoto );
        for(DataSnapshot userDataSnapshot : dataSnapshot.getChildren()){
            if(userDataSnapshot.getKey().equals("users")){
                HashMap<String,HashMap<String,String>> aux = (HashMap<String,HashMap<String,String>>)userDataSnapshot.getValue();
                Iterator it = aux.keySet().iterator();
                while(it.hasNext()){
                    String key = it.next().toString();
                    HashMap<String,String> aux2 = aux.get(key);
                    Iterator it2 = aux2.keySet().iterator();
                    while(it2.hasNext()){
                        String key2 = it2.next().toString();
                        if(key2.equals("id")){
                            idUser = aux2.get(key2);
                            Log.d("id of user: ",idUser);
                        }else if(key2.equals("name")){
                            nUser = aux2.get(key2);
                            Log.d("name of user:",nUser);
                        }else if(key2.equals("email")){
                            emailUser = aux2.get(key2);
                            Log.d("name of user:",emailUser);
                        }else if(key2.equals("url")){
                            urlUser = aux2.get(key2);
                            Log.d("name of user:",urlUser);
                        }

                    }
                    User userToAdd = new User(idUser,nUser,emailUser,urlUser);
                    UserMap = new HashMap<String,User> ();
                    UserMap.put(idUser,userToAdd);
                }
                Log.d("value: ","lo tengo");
            }
        }
        }else{
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.noexistactivity), Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }


}
