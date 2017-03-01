package store.singto.singtostore.MeTab;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import store.singto.singtostore.ProductTab.DetailPrdActivity;
import store.singto.singtostore.R;
import store.singto.singtostore.Tools.Tools;


public class FavoritePrdActivity extends AppCompatActivity {
    private RecyclerView favoritePrdRcyv;
    private List<FavoritePrd> favs;
    private FirebaseAuth auth;
    private DatabaseReference reference,prdRef;
    private ChildEventListener listener;
    private ValueEventListener prdListener;
    private FavAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_prd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        favs = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        prdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    FavoritePrd prd = new FavoritePrd();
                    prd.pKey = dataSnapshot.getKey();
                    prd.pName = dataSnapshot.child("productName").getValue().toString();
                    prd.pPrice = dataSnapshot.child("productPrice").getValue().toString();
                    List<String> imgs = (List<String>) dataSnapshot.child("productImages").getValue();
                    prd.pImg = imgs.get(0);
                    favs.add(0,prd);
                    adapter.notifyItemInserted(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String prdkey = dataSnapshot.getKey();
                loadFavBykey(prdkey);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String prdkey = dataSnapshot.getKey();
                //delete from list
                for(int i=0;i<favs.size();i++){
                    if(favs.get(i).pKey.equals(prdkey)){
                        favs.remove(i);
                        adapter.notifyItemRemoved(i);
                    }
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        prdRef = FirebaseDatabase.getInstance().getReference().child("AllProduct");
        favoritePrdRcyv = (RecyclerView)findViewById(R.id.favoritePrd_Rcyv);
        favoritePrdRcyv.setLayoutManager(new LinearLayoutManager(this));
        favoritePrdRcyv.addItemDecoration(new Tools.RecyPadding(0,6,4,6));
        adapter = new FavAdapter(this,favs);
        adapter.setOnItemClickListener(new FavAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                String key = favs.get(index).pKey;
                Intent intent = new Intent(FavoritePrdActivity.this, DetailPrdActivity.class);
                intent.putExtra("prdKey", key);
                startActivity(intent);
            }
        });
        favoritePrdRcyv.setAdapter(adapter);
        //handle delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if(auth.getCurrentUser()!=null){
                    String uid = auth.getCurrentUser().getUid();
                    String pid = favs.get(position).pKey;
                    System.out.println(pid);
                    reference.child(uid).child("FavoritePRD").child(pid).removeValue();
                }
            }
            //no Move support
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
        }).attachToRecyclerView(favoritePrdRcyv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser()!=null){
            String uid = auth.getCurrentUser().getUid();
            reference.child(uid).child("FavoritePRD").addChildEventListener(listener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        favs.clear();
        adapter.notifyDataSetChanged();
        if(auth.getCurrentUser()!=null){
            String uid = auth.getCurrentUser().getUid();
            reference.child(uid).child("FavoritePRD").removeEventListener(listener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavBykey(String key){
        prdRef.child(key).addListenerForSingleValueEvent(prdListener);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView img;
        public TextView prdName, price;
        public ViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.favorite_prd_cell, viewGroup,false));
            img = (ImageView) itemView.findViewById(R.id.favroitePrd_img);
            prdName = (TextView) itemView.findViewById(R.id.favoritePrd_name);
            prdName.setEllipsize(TextUtils.TruncateAt.END);
            price = (TextView) itemView.findViewById(R.id.favoritePrd_price);
        }
    }

    public static class FavAdapter extends RecyclerView.Adapter<ViewHolder>{
        private Context context;
        private List<FavoritePrd> list;

        public FavAdapter(Context context, List<FavoritePrd> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final FavoritePrd p = list.get(position);
            holder.prdName.setText(p.pName);
            holder.price.setText("THB "+ p.pPrice+ ".0");
            Picasso.with(context).load(p.pImg).placeholder(R.drawable.placeholder).into(holder.img);

            if(itemClickListener!=null){
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int index = holder.getLayoutPosition();
                        itemClickListener.onItemClick(holder.itemView,index);
                    }
                });
            }

        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return list.size();
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
