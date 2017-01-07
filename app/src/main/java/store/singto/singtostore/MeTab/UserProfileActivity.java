package store.singto.singtostore.MeTab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import store.singto.singtostore.R;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference database;
    private RecyclerView recyclerView;
    private static List<String> items, info;
    private ProfileAdapter mAdapter;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setupView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView(){
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("users");
        getSupportActionBar().setElevation(0); //remove shadow of action bar
        items = new ArrayList<>();
        info = new ArrayList<>();
        info.add(0,"");
        info.add(1,"");
        info.add(2,"");
        items.add(0,"PROFILE PHOTO");
        items.add(1,"NAME");
        items.add(2,"PHONE NUMBER");
        recyclerView = (RecyclerView)findViewById(R.id.userProfileRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ProfileAdapter();
        mAdapter.setOnItemClickListener(new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                switch (index){
                    case 0:
                        Intent intent1 = new Intent(UserProfileActivity.this, EditUserAvatarActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent = new Intent(UserProfileActivity.this, EditUserNameActivity.class);
                        intent.putExtra("name", info.get(1));
                        startActivity(intent);
                        break;
                    case 2:
                        Intent i = new Intent(UserProfileActivity.this, EditUserPhoneActivity.class);
                        i.putExtra("phone", info.get(2));
                        startActivity(i);
                        break;
                    default:
                        break;
                }
            }
        });
        recyclerView.setAdapter(mAdapter);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").getValue()!=null){
                    String name = dataSnapshot.child("name").getValue().toString();
                    info.set(1, name);
                }
                if(dataSnapshot.child("phone").getValue()!=null){
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    info.set(2, phone);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userProfileItem, userProfileItemDetail;
        public ViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.userprofilecell, viewGroup, false));
            userProfileItem = (TextView) itemView.findViewById(R.id.userProfileItem);
            userProfileItemDetail = (TextView) itemView.findViewById(R.id.userProfileItemDetail);
        }
    }

    public static class ProfileAdapter extends RecyclerView.Adapter<ViewHolder>{
//        public ProfileAdapter(Context context){
//
//        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.userProfileItem.setText(items.get(position));
            holder.userProfileItemDetail.setText(info.get(position));
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
            return items.size();
        }

        public interface OnItemClickListener{
            void onItemClick(View view, int index);
        }

        private OnItemClickListener itemClickListener;
        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.itemClickListener = onItemClickListener;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.removeEventListener(listener);
    }

    private void getUserInfo(){
        if(auth.getCurrentUser().getUid()!=null){
            String uid = auth.getCurrentUser().getUid();
            database.child(uid).child("USERINFO").addValueEventListener(listener);
        }
    }

}
