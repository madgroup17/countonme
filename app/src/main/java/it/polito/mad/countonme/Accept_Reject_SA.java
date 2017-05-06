package it.polito.mad.countonme;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Iterator;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.models.*;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Created by LinaMaria on 22/04/2017.
 */

public class Accept_Reject_SA extends AppCompatActivity  implements  ValueEventListener {
    private FragmentManager mFragmentManager;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private TextView mNameSADetails;
    private TextView mDetailsSADetails;
    private TextView mCurrencySADetails;
    private boolean vacio;
    HashMap<String,User> UserMap;
    String nUser;
    String idUser;
    String emailUser;
    String urlUser;

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Here the data arrives
        //Log.d("Hello", "ello"); // here are your data as you can see
        SharingActivity myActivity = (SharingActivity) dataSnapshot.getValue(SharingActivity.class);
        mNameSADetails.setText(myActivity.getName());
        mDetailsSADetails.setText(myActivity.getDescription());
        mCurrencySADetails.setText(myActivity.getCurrency());
        //DataManager.getsInstance().getSharingActivityReference(path).child("users").addListenerForSingleValueEvent(this);

//TODO: Si no existe ningun usuario atado (puedo controlarlo con un booleano si ya recorrio todo el datasnashot), debe inicializar el user con los datos de la busqueda del user en la rama de user no en la rama de sharing
      //  HashMap<String, String> UserMap =  new HashMap<String,User>();
        for(DataSnapshot userDataSnapshot : dataSnapshot.getChildren()){
            if(userDataSnapshot.getKey().equals("users")){
                //Log.d("value: ", String.valueOf((HashMap<String,User>)userDataSnapshot.getValue()));
                //UserMap = (HashMap<String,User>)userDataSnapshot.getValue();

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
        //vacio=UserMap.isEmpty();
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_reject_sa);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // I can assume that the sharing activty key arrives here as intent's data
        Intent intent = getIntent();
        path = intent.getData().getPath();
        path = path.substring(1);
        DataManager.getsInstance().getSharingActivityReference(path).addListenerForSingleValueEvent(this);

        mNameSADetails = (TextView) findViewById(R.id.sharing_activity_name_sa);
        mDetailsSADetails = (TextView) findViewById(R.id.sharing_activity_description_sa);
        mCurrencySADetails = (TextView) findViewById(R.id.sharing_activity_currency_sa);

        SharingActivity model = new SharingActivity();

        final Button buttonAccept = (Button) findViewById(R.id.accept_sa);

        buttonAccept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Perform action on click
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(Accept_Reject_SA.this, "User didnt do log in", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // the user is logged in so we will show the application first screen
                    Intent llave = new Intent(Intent.ACTION_SEND);
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String mostrar = "   " + currentFirebaseUser.getUid() + "    " + path+"  "+vacio;
                    DataManager.getsInstance().getSharingActivityReference(path).child("users").addListenerForSingleValueEvent(Accept_Reject_SA.this);
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
                    DatabaseReference actuserref = ref.child("shareacts").child(path).child("users");
                    User current = new User(currentFirebaseUser.getUid(),currentFirebaseUser.getDisplayName(),currentFirebaseUser.getEmail(),null);//(idUser,nUser,emailUser,urlUser);
                    actuserref.child(currentFirebaseUser.getUid()).setValue(current);

                    //redirect to sharing activity item list
                    finish();
                    startActivity(new Intent(Accept_Reject_SA.this, it.polito.mad.countonme.CountOnMeActivity.class));
                }
            }
        });
        final Button buttonReject = (Button) findViewById(R.id.reject_sa);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast.makeText(Accept_Reject_SA.this, "click on reject", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
