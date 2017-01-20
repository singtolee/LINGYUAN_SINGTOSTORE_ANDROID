package store.singto.singtostore.ProductTab;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import store.singto.singtostore.MeTab.EditUserFreeAddressActivity;
import store.singto.singtostore.MeTab.FreeAddress;
import store.singto.singtostore.R;
import store.singto.singtostore.Tools.Tools;

public class BuyNowActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private RelativeLayout relativeLayout;
    private int csID;
    private String prdKey;
    private int MAXQTY;
    private int price;
    private String URL;

    private DatabaseReference qtyRef, addressRef, prdRef, publicOrderRef;
    private ValueEventListener qtyListener, addressListener, prdListener;

    private TextView prdMaxQtyHint, qtyIncreaseBtn, qtyDecreaseBtn, qtyIndicator, namephone, buildingroom, prdTitle, prdCSTV, prdTotalTV;
    private ImageView prdCSImg;
    private Button checkoutBtn;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(BuyNowActivity.this);
        progressDialog.setMessage(getString(R.string.submitting));
        setContentView(R.layout.activity_buy_now);
        relativeLayout = (RelativeLayout)findViewById(R.id.activity_buy_now);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        csID = bundle.getInt("csID");
        prdKey = bundle.getString("prdKey");
        prdRef = FirebaseDatabase.getInstance().getReference().child("AllProduct").child(prdKey);
        qtyRef = FirebaseDatabase.getInstance().getReference().child("AllProduct").child(prdKey).child("prodcutCSQty").child(Integer.toString(csID));
        addressRef = FirebaseDatabase.getInstance().getReference().child("FreeDeliveryAddresses");
        publicOrderRef = FirebaseDatabase.getInstance().getReference().child("PUBLICORDERS");
        addressListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    FreeAddress address = new FreeAddress();
                    address = dataSnapshot.getValue(FreeAddress.class);
                    namephone.setText("TO: " + address.recipient + " " + address.phone);
                    buildingroom.setText("ADDRESS: " + address.roomNumber + ", " + address.officeBuilding);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        qtyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    String num = dataSnapshot.getValue().toString();
                    MAXQTY = Integer.parseInt(num);
                    prdMaxQtyHint.setText("("+num+"LEFT)");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        prdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    String prdName = dataSnapshot.child("productName").getValue().toString();
                    prdTitle.setText(prdName);
                    String prdPriceStr = dataSnapshot.child("productPrice").getValue().toString();
                    price = Integer.parseInt(prdPriceStr);
                    prdTotalTV.setText("TOTAL: THB " + prdPriceStr + ".0");
                    String prdCS = dataSnapshot.child("prodcutCS").child(Integer.toString(csID)).getValue().toString();
                    prdCSTV.setText(prdCS);
                    String url = dataSnapshot.child("productImages").child(Integer.toString(csID)).getValue().toString();
                    URL = url;
                    Picasso.with(BuyNowActivity.this).load(url).placeholder(R.drawable.placeholder).into(prdCSImg);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        setUpUIs();

    }

    @Override
    protected void onStart() {
        super.onStart();
        qtyRef.addValueEventListener(qtyListener);
        if(auth.getCurrentUser()!=null){
            String uid = auth.getCurrentUser().getUid();
            addressRef.child(uid).addListenerForSingleValueEvent(addressListener);
        }

        prdRef.addListenerForSingleValueEvent(prdListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        qtyRef.removeEventListener(qtyListener);


    }

    private void setUpUIs(){
        checkoutBtn = (Button)findViewById(R.id.checkoutsingleprd);
        prdTotalTV = (TextView)findViewById(R.id.prdTotalMoney);
        prdCSImg = (ImageView)findViewById(R.id.prdCSImg);
        prdTitle = (TextView)findViewById(R.id.prdBigTitle);
        prdCSTV = (TextView)findViewById(R.id.prdsmallCS);
        namephone = (TextView)findViewById(R.id.namephone);
        buildingroom = (TextView)findViewById(R.id.buildingroom);
        prdMaxQtyHint = (TextView)findViewById(R.id.prdMaxQtyHint);
        qtyIndicator = (TextView)findViewById(R.id.qtyIndicator);
        qtyIncreaseBtn = (TextView)findViewById(R.id.qtyIncreaseBtn);
        qtyDecreaseBtn = (TextView)findViewById(R.id.qtyDecreaseBtn);
        qtyIncreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQty = Integer.parseInt(qtyIndicator.getText().toString());
                currentQty = currentQty + 1;
                if(currentQty>MAXQTY){
                    currentQty = MAXQTY;
                }
                qtyIndicator.setText(Integer.toString(currentQty));
                String tm = Integer.toString(price*currentQty);
                prdTotalTV.setText("TOTOAL: THB " + tm+".0");

            }
        });
        qtyDecreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQty = Integer.parseInt(qtyIndicator.getText().toString());
                currentQty = currentQty - 1;
                if(currentQty<1){
                    currentQty=1;
                }
                qtyIndicator.setText(Integer.toString(currentQty));
                String tm = Integer.toString(price*currentQty);
                prdTotalTV.setText("TOTOAL: THB " + tm+".0");
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(price>0){
                    safeCheckOut();
                }
            }
        });
    }

    private void safeCheckOut(){
        progressDialog.show();
        qtyRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue() == null){
                    return Transaction.success(mutableData);
                }else {
                    String cqty = mutableData.getValue().toString();
                    int cnum = Integer.parseInt(cqty);  //how many left
                    int amount = Integer.parseInt(qtyIndicator.getText().toString()); //amount to buy
                    int leftNum = cnum-amount;
                    if(leftNum>=0){
                        mutableData.setValue(Integer.toString(leftNum));
                        return Transaction.success(mutableData);
                    }else {
                        //not enough in stock
                        return Transaction.abort();
                    }
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError!=null){
                    //handle unknow error
                    progressDialog.dismiss();
                    makeAlert("UNKNOW ERROR",databaseError.getDetails().toString());

                }else {
                    if (b){
                        //now can write to public and user orders
                        writePublicAndUserOrders();
                    }else {
                        //low Stock hint
                        progressDialog.dismiss();
                        makeAlert("Low in Stock", "This product is low in stock, please reduce the quantity or buy other product.");

                    }
                }

            }
        });
    }

    private void writePublicAndUserOrders(){
        OrderPrd prd = new OrderPrd();
        prd.Qty = Integer.parseInt(qtyIndicator.getText().toString());
        prd.cs = prdCSTV.getText().toString();
        prd.date = Tools.getDateOnly();
        prd.prdKey = prdKey;
        prd.price = price;
        prd.selectedCSID = csID;
        prd.status = 0;
        prd.time = Tools.getTimeOnly();
        prd.title = prdTitle.getText().toString();
        prd.url = URL;
        prd.userKey = auth.getCurrentUser().getUid();
        String orderKey = publicOrderRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/PUBLICORDERS/" + prd.date + "/" + orderKey, prd);
        childUpdates.put("/users/" + prd.userKey + "/Orders/" + orderKey, prd);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    makeAlert("SUCCESS","Your order will arrive within 24 Hours, Check it at ORDERS.");
                }else {
                    makeAlert("ERROR",task.getException().getLocalizedMessage());
                }
            }
        });
    }

    private void makeAlert(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(BuyNowActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
