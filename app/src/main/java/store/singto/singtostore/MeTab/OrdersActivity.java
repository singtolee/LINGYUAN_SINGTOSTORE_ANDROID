package store.singto.singtostore.MeTab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import store.singto.singtostore.ProductTab.OrderPrd;
import store.singto.singtostore.R;

public class OrdersActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference orderRef;
    private List<OrderPrd> orders;
    private ChildEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auth = FirebaseAuth.getInstance();
        orderRef = FirebaseDatabase.getInstance().getReference().child("users");
        orders = new ArrayList<>();
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                OrderPrd oo = dataSnapshot.getValue(OrderPrd.class);
                orders.add(oo);
                System.out.println(oo.title);
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
}
