package store.singto.singtostore.MeTab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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

    private LoginManager loginManager;

    private Button loginRegisterBtn, exitAppBtn;
    private ImageView userAvatarImgv;
    private TextView userName;

    private RecyclerView functions;
    private FuncAdapter funcAdapter;
    private String[] funcs;
    private int[] icons;

    public MeTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_tab, container, false);
        icons = new int[]{
                R.drawable.ic_assignment_black_24dp,
                R.drawable.ic_place_black_24dp,
                R.drawable.ic_favorite_red_24dp,
                R.drawable.ic_account_circle_black_24dp,
                R.drawable.ic_room_service_black_24dp,
                R.drawable.ic_share_black_24dp
        };
        funcs = new String[]{
          "ORDERS","ADDRESS","FAVORITE","PROFILE","CONTACT US","SHARE ME"
        };
        setupView(view);
        return view;
    }

    private void setupView(View view){
        mAuth = FirebaseAuth.getInstance();

        userAvatarImgv = (ImageView) view.findViewById(R.id.userAvatar);
        userName = (TextView) view.findViewById(R.id.userName);
        loginRegisterBtn = (Button) view.findViewById(R.id.loginRegisterBtn);
        exitAppBtn = (Button) view.findViewById(R.id.exitAppBtn);
        loginManager = LoginManager.getInstance();

        userAvatarImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to edit user profile
                if(mAuth.getCurrentUser()!=null){
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    startActivity(intent);
                }
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            }
        });

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
                loginManager.logOut();
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

        functions = (RecyclerView) view.findViewById(R.id.userFunctionsLV);
        functions.setLayoutManager(new LinearLayoutManager(getContext()));
        funcAdapter = new FuncAdapter(getContext(), funcs, icons);
        funcAdapter.setOnItemClickListener(new FuncAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                switch (index){
                    case 0:
                        if(mAuth.getCurrentUser()!=null){
                            Intent orderIntent = new Intent(getActivity(),OrdersActivity.class);
                            startActivity(orderIntent);
                        }else {
                            Toast.makeText(getContext(),R.string.tellUsertologin,Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        if(mAuth.getCurrentUser()!=null){
                            Intent intent1 = new Intent(getActivity(),EditUserFreeAddressActivity.class);
                            startActivity(intent1);
                        }else {
                            Toast.makeText(getContext(),R.string.tellUsertologin,Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 2:
                        if(mAuth.getCurrentUser()!=null){
                            Intent fav = new Intent(getActivity(), FavoritePrdActivity.class);
                            startActivity(fav);
                        }else {
                            Toast.makeText(getContext(),R.string.tellUsertologin,Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 3:
                        if(mAuth.getCurrentUser()!=null){
                            Intent profile = new Intent(getActivity(), UserProfileActivity.class);
                            startActivity(profile);
                        }else {
                            Toast.makeText(getContext(),R.string.tellUsertologin,Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    default:
                        break;
                }
            }
        });

        functions.setAdapter(funcAdapter);

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView funcName;
        public ImageView funcIcon;
        public ViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.userfuncscell, viewGroup, false));
            funcName = (TextView) itemView.findViewById(R.id.funcName);
            funcIcon = (ImageView) itemView.findViewById(R.id.funcIcon);
        }
    }

    public static class FuncAdapter extends RecyclerView.Adapter<ViewHolder>{
        private Context context;
        private int[] ics;
        private String[] funcs;

        public FuncAdapter(Context context, String[] f, int[] ic){
            this.context = context;
            this.funcs = f;
            this.ics = ic;

        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.funcName.setText(funcs[position]);
            holder.funcIcon.setImageResource(ics[position]);
            if(itemClickListener!=null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = holder.getLayoutPosition();
                        itemClickListener.onItemClick(holder.itemView, index);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return funcs.length;
        }

        public interface OnItemClickListener{
            void onItemClick(View view, int index);
        }

        private OnItemClickListener itemClickListener;
        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.itemClickListener = onItemClickListener;
        }
    }

}
