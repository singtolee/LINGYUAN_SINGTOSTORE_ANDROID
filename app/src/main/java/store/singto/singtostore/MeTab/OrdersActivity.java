package store.singto.singtostore.MeTab;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import store.singto.singtostore.ProductTab.OrderPrd;
import store.singto.singtostore.R;

public class OrdersActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference orderRef;
    private List<OrderPrd> orders;
    private ChildEventListener listener;
    private RecyclerView orderRecyv;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        orders = new ArrayList<>();

        orderRecyv = (RecyclerView)findViewById(R.id.ordersRecyv);
        orderRecyv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this,orders);
        orderRecyv.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        orderRef = FirebaseDatabase.getInstance().getReference().child("users");
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                OrderPrd oo = dataSnapshot.getValue(OrderPrd.class);
                orders.add(oo);
                adapter.notifyItemInserted(orders.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String uid = auth.getCurrentUser().getUid();
        if(uid!=null){
            orders.clear();
            orderRef.child(uid).child("Orders").addChildEventListener(listener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String uid = auth.getCurrentUser().getUid();
        if(uid!=null){
            orderRef.child(uid).child("Orders").removeEventListener(listener);
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView img;
        public TextView prdName, price,prdCS,dateTime;
        public ViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.order_prd_cell, viewGroup,false));
            img = (ImageView) itemView.findViewById(R.id.orderPrdImg);
            prdName = (TextView) itemView.findViewById(R.id.orderPrdTitle);
            prdName.setEllipsize(TextUtils.TruncateAt.END);
            price = (TextView)itemView.findViewById(R.id.orderPrdPrice);
            prdCS = (TextView) itemView.findViewById(R.id.orderPrdCS);
            dateTime = (TextView)itemView.findViewById(R.id.orderDateTime);
        }
    }

    public static class OrderAdapter extends RecyclerView.Adapter<ViewHolder>{
        private Context context;
        private List<OrderPrd> list;
        public OrderAdapter(Context context,List<OrderPrd> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final OrderPrd p = list.get(position);
            Picasso.with(context).load(p.url).placeholder(R.drawable.placeholder).into(holder.img);
            holder.prdName.setText(p.title);
            holder.prdCS.setText(p.cs+"*"+Integer.toString(p.Qty));
            holder.price.setText("THB " + Integer.toString(p.Qty*p.price)+ ".0");
            holder.dateTime.setText(p.time+ " " + p.date);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


    }


}
