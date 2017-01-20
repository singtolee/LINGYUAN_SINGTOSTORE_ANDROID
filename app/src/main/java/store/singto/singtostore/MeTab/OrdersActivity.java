package store.singto.singtostore.MeTab;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
                //orders.add(oo);
                //adapter.notifyItemInserted(orders.size()-1);
                orders.add(0,oo);
                adapter.notifyItemInserted(0);
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

        public ImageView firstDot, middleDot, lastDot;
        public View firstLine, secondLine;
        public TextView confirmStatus, shippingStatus, doneStatus;

        public ViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.order_prd_cell, viewGroup,false));
            img = (ImageView) itemView.findViewById(R.id.orderPrdImg);
            prdName = (TextView) itemView.findViewById(R.id.orderPrdTitle);
            prdName.setEllipsize(TextUtils.TruncateAt.END);
            price = (TextView)itemView.findViewById(R.id.orderPrdPrice);
            prdCS = (TextView) itemView.findViewById(R.id.orderPrdCS);
            dateTime = (TextView)itemView.findViewById(R.id.orderDateTime);
            //for status bar
            firstDot = (ImageView)itemView.findViewById(R.id.firstDot);
            middleDot = (ImageView)itemView.findViewById(R.id.middleDot);
            lastDot = (ImageView)itemView.findViewById(R.id.lastDot);
            firstLine = itemView.findViewById(R.id.orderFirstLine);
            secondLine = itemView.findViewById(R.id.orderSecondLine);
            confirmStatus = (TextView)itemView.findViewById(R.id.orderConfirmedStatusText);
            shippingStatus = (TextView)itemView.findViewById(R.id.orderShippingStatusText);
            doneStatus = (TextView)itemView.findViewById(R.id.orderDoneStatusText);
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
            int colorR = Color.parseColor("#FF3845");
            int colorG = Color.parseColor("#A9A9A9");
            int colorB = Color.parseColor("#000000");
            switch (p.status){
                case -1:
                    //order cancelled
                    holder.firstDot.setColorFilter(colorB);
                    holder.middleDot.setColorFilter(colorG);
                    holder.lastDot.setColorFilter(colorG);
                    holder.firstLine.setBackgroundColor(colorG);
                    holder.secondLine.setBackgroundColor(colorG);
                    holder.confirmStatus.setText("CANCELLED");
                    holder.confirmStatus.setTextColor(colorB);
                    holder.shippingStatus.setVisibility(View.INVISIBLE);
                    holder.doneStatus.setVisibility(View.INVISIBLE);

                    break;
                case 0:
                    holder.firstDot.setColorFilter(colorR);
                    holder.middleDot.setColorFilter(colorG);
                    holder.lastDot.setColorFilter(colorG);
                    holder.firstLine.setBackgroundColor(colorG);
                    holder.secondLine.setBackgroundColor(colorG);
                    holder.confirmStatus.setText("CONFIRMED");
                    holder.confirmStatus.setTextColor(colorR);
                    holder.shippingStatus.setVisibility(View.INVISIBLE);
                    holder.doneStatus.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    //shipping out
                    holder.firstDot.setColorFilter(colorR);
                    holder.middleDot.setColorFilter(colorR);
                    holder.lastDot.setColorFilter(colorG);
                    holder.firstLine.setBackgroundColor(colorR);
                    holder.secondLine.setBackgroundColor(colorG);
                    holder.confirmStatus.setText("CONFIRMED");
                    holder.confirmStatus.setTextColor(colorR);
                    holder.shippingStatus.setText("ON THE WAY");
                    holder.shippingStatus.setTextColor(colorR);
                    holder.doneStatus.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    // order is done
                    holder.firstDot.setColorFilter(colorR);
                    holder.middleDot.setColorFilter(colorR);
                    holder.lastDot.setColorFilter(colorR);
                    holder.firstLine.setBackgroundColor(colorR);
                    holder.secondLine.setBackgroundColor(colorR);
                    holder.confirmStatus.setText("CONFIRMED");
                    holder.confirmStatus.setTextColor(colorR);
                    holder.shippingStatus.setText("ON THE WAY");
                    holder.shippingStatus.setTextColor(colorR);
                    holder.doneStatus.setText("DONE");
                    holder.doneStatus.setTextColor(colorR);
                    break;
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


    }


}
