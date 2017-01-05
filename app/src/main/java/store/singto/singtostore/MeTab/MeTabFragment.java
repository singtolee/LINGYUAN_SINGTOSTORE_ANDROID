package store.singto.singtostore.MeTab;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import store.singto.singtostore.LoginRegister.LoginActivity;
import store.singto.singtostore.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeTabFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private Button loginRegisterBtn, exitAppBtn;
    private ImageView userAvatarImgv;
    private TextView userName;

    public MeTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_tab, container, false);

        mAuth = FirebaseAuth.getInstance();

        userAvatarImgv = (ImageView) view.findViewById(R.id.userAvatar);
        userName = (TextView) view.findViewById(R.id.userName);
        loginRegisterBtn = (Button) view.findViewById(R.id.loginRegisterBtn);
        exitAppBtn = (Button) view.findViewById(R.id.exitAppBtn);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userinstatus();
                    //get user info from database
                    fetchUserInfo(user.getUid());

                }else {
                    useroutstatus();
                }
            }
        };

        exitAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout , clean userAvatar, name, hide exitAppBtn, hide userName
                mAuth.signOut();
            }
        });

        loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to login page
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void userinstatus() {
        loginRegisterBtn.setVisibility(View.GONE);
        userName.setVisibility(View.VISIBLE);
        exitAppBtn.setVisibility(View.VISIBLE);

    }

    private void useroutstatus() {
        userAvatarImgv.setImageResource(R.drawable.ic_account_circle_white_48dp);
        loginRegisterBtn.setVisibility(View.VISIBLE);
        userName.setVisibility(View.GONE);
        userName.setText(getString(R.string.defaultUserName));
        exitAppBtn.setVisibility(View.INVISIBLE);
    }

    private void fetchUserInfo(final String uid) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("USERINFO");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    if(dataSnapshot.child("name").getValue() != null) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        userName.setText(name);
                    }
                    if(dataSnapshot.child("userAvatarUrl").getValue() != null) {
                        String url = dataSnapshot.child("userAvatarUrl").getValue().toString();
                        Picasso.with(getActivity()).load(url).into(userAvatarImgv);

                    }
                }else {
                    //user info is empty
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(listener);
    }

}
