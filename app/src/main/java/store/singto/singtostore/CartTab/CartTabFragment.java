package store.singto.singtostore.CartTab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import store.singto.singtostore.MainActivity;
import store.singto.singtostore.MeTab.EditUserFreeAddressActivity;
import store.singto.singtostore.MeTab.FreeAddress;
import store.singto.singtostore.ProductTab.CartPrd;
import store.singto.singtostore.ProductTab.OrderPrd;
import store.singto.singtostore.R;
import store.singto.singtostore.Tools.Tools;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CartTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartTabFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener stateListener;
    private DatabaseReference reference,addressRef,allPrdRef;
    private ChildEventListener listener;
    private List<CartPrd> carts, ckCarts;
    private RecyclerView cartRV;
    private CartAdapter adapter;

    private LinearLayout bottomLL;
    private TextView totalPrice;
    private Button checkAllOutBtn;

    private String UID;  //

    private FreeAddress address;

    private boolean isLowQty = false;

    private ProgressDialog progressDialog;

    public CartTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_cart_tab, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.submitting));
        carts = new ArrayList<>();
        ckCarts = new ArrayList<>();
        address = new FreeAddress();
        addressRef = FirebaseDatabase.getInstance().getReference().child("FreeDeliveryAddresses");
        auth = FirebaseAuth.getInstance();
        stateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    UID = user.getUid();
                    carts.clear();
                    ckCarts.clear();
                    adapter.notifyDataSetChanged();
                    //listen childEvent, show bottom bar
                    reference.child(UID).child("SHOPPINGCART").addChildEventListener(listener);

                }else {
                    //clear all, hide bottom bar
                    carts.clear();
                    ckCarts.clear();
                    adapter.notifyDataSetChanged();
                    updateBottomBar();
                    if(UID!=null){
                        reference.child(UID).child("SHOPPINGCART").removeEventListener(listener);
                    }
                }

            }
        };
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        allPrdRef = FirebaseDatabase.getInstance().getReference().child("AllProduct");
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CartPrd c = dataSnapshot.getValue(CartPrd.class);
                carts.add(0,c);
                adapter.notifyItemInserted(0);
                updateBottomBar();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String ck = dataSnapshot.getKey();
                CartPrd c = dataSnapshot.getValue(CartPrd.class);
                int changedIndex = findIndexByKey(ck);
                if(changedIndex!=-1){
                    carts.set(changedIndex,c);
                    updateBottomBar();
                    adapter.notifyItemChanged(changedIndex);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String ck = dataSnapshot.getKey();
                int changedIndex = findIndexByKey(ck);
                if(changedIndex!=-1){
                    //
                    carts.remove(changedIndex);
                    updateBottomBar();
                    adapter.notifyItemRemoved(changedIndex);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        cartRV = (RecyclerView) view.findViewById(R.id.cartRV);
        cartRV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(getContext(), carts);
        cartRV.setAdapter(adapter);
        //bottom bar
        bottomLL = (LinearLayout) view.findViewById(R.id.cartTab_bottombar);
        bottomLL.setVisibility(View.INVISIBLE);

        totalPrice = (TextView) view.findViewById(R.id.cartTotolPrice);
        checkAllOutBtn = (Button)view.findViewById(R.id.cartCheckBtn);
        checkAllOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressRef.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null){
                            address = dataSnapshot.getValue(FreeAddress.class);
                            String alertTitle = "Are you sure to make this order?";
                            String alertMsg = "Deliever to: " + address.recipient + " ," + address.phone + " ," + address.roomNumber + " ," + address.officeBuilding;
                            makeAlert(alertTitle, alertMsg);

                        }else {
                            //no address yet
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("No Address Yet");
                            builder.setMessage("Please edit your address first").setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //go to edit address
                                    Intent addreeIntent = new Intent(getContext(), EditUserFreeAddressActivity.class);
                                    startActivity(addreeIntent);

                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        auth.addAuthStateListener(stateListener);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteCartByIndex(position);
            }

            //no Move support
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

        }).attachToRecyclerView(cartRV);

        return view;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{
        public ImageView check, img;
        public TextView cartTitle, cartCS, cartPrice, qty, minusBtn, plusBtn;
        public CartViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.cart_cell, viewGroup,false));
            check = (ImageView) itemView.findViewById(R.id.isChecked);
            img = (ImageView) itemView.findViewById(R.id.cartMainImg);
            cartTitle = (TextView) itemView.findViewById(R.id.cartPrdName);
            cartTitle.setSingleLine();
            cartTitle.setEllipsize(TextUtils.TruncateAt.END);
            cartCS = (TextView) itemView.findViewById(R.id.cartPrdCS);
            cartPrice = (TextView) itemView.findViewById(R.id.cartPrdPrice);
            qty = (TextView) itemView.findViewById(R.id.cartQty);
            minusBtn = (TextView) itemView.findViewById(R.id.cartMinusBtn);
            plusBtn = (TextView) itemView.findViewById(R.id.cartPlusBtn);
        }
    }

    public static class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{
        private Context context;
        private List<CartPrd> list;

        public CartAdapter(Context context, List<CartPrd> list){
            this.context = context;
            this.list = list;
        }
        @Override
        public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CartViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(final CartViewHolder holder, int position) {
            final CartPrd p = list.get(position);
            if(p.Check){
                holder.check.setImageResource(R.drawable.checked);
            }else {
                holder.check.setImageResource(R.drawable.unchecked);
            }
            holder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            Picasso.with(context).load(p.prdImg).placeholder(R.drawable.placeholder).into(holder.img);
            holder.cartTitle.setText(p.prdTitle);
            holder.cartCS.setText(p.prdCS);
            holder.cartPrice.setText("THB "+p.prdPrice+".0");
            holder.qty.setText(Integer.toString(p.Qty));

            holder.plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if(auth.getCurrentUser()!=null){
                        final String uid = auth.getCurrentUser().getUid();
                        DatabaseReference qtyRef = FirebaseDatabase.getInstance().getReference().child("AllProduct").child(p.prdKey).child("prodcutCSQty").child(Integer.toString(p.ID));
                        qtyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final int max = Integer.parseInt(dataSnapshot.getValue().toString());
                                if(max>0){
                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("SHOPPINGCART").child(p.cartKey).child("Qty");
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            int num = Integer.parseInt(dataSnapshot.getValue().toString());
                                            num = num + 1;
                                            if(num>max){
                                                num = max;
                                            }
                                            ref.setValue(num);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                }
            });

            holder.minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if(auth.getCurrentUser()!=null){
                        String uid = auth.getCurrentUser().getUid();
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("SHOPPINGCART").child(p.cartKey).child("Qty");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int num = Integer.parseInt(dataSnapshot.getValue().toString());
                                num = num - 1;
                                if(num<1){
                                    num = 1;
                                }
                                ref.setValue(num);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                }
            });

            holder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if(auth.getCurrentUser()!=null){
                        String uid = auth.getCurrentUser().getUid();
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("SHOPPINGCART").child(p.cartKey).child("Check");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean isChecked = (boolean) dataSnapshot.getValue();
                                if(isChecked){
                                    ref.setValue(false);

                                }else {
                                    ref.setValue(true);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private int findIndexByKey(String key){
        int de = -1;
        for(int i=0;i<carts.size();i++){
            if(carts.get(i).cartKey.equals(key)){
                de = i;
                break;
            }
        }
        return de;
    }

    private void updateBottomBar(){
        //TODO Badge update!!!
        if(carts.size()>0){
            ( (MainActivity)getActivity()).updateBadge(Integer.toString(carts.size()));
        }else {
            ( (MainActivity)getActivity()).updateBadge("");
        }
        int item = 0;
        int total = 0;
        for(CartPrd cc : carts){
            if(cc.Check){
                item = item + cc.Qty;
                total = total + cc.Qty*cc.prdPrice;
            }
        }

        if (item>0){
            totalPrice.setText("TOTOAL: THB "+Integer.toString(total)+".0");
            bottomLL.setVisibility(View.VISIBLE);
        }else{
            bottomLL.setVisibility(View.INVISIBLE);
        }

    }

    private void deleteCartByIndex(int position){
        String ckey = carts.get(position).cartKey;
        if (auth.getCurrentUser()!=null){
            String uid = auth.getCurrentUser().getUid();
            reference.child(uid).child("SHOPPINGCART").child(ckey).removeValue();
        }

    }

    private void makeAlert(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(msg).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //start to run Transaction check out
                //List<CartPrd> myCarts = new ArrayList<>();
                //display progress dialog
                progressDialog.show();
                ckCarts.clear();
                for(CartPrd cc : carts){
                    if(cc.Check){
                        ckCarts.add(cc);
                    }
                }

                int geshu = ckCarts.size();
                isLowQty = false;
                for(int i=0;i<geshu;i++){
                    safeCheckout(ckCarts.get(i),i+1,geshu);
                }

            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing at here
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void safeCheckout(final CartPrd prd, final int i, final int cishu){
        allPrdRef.child(prd.prdKey).child("prodcutCSQty").child(Integer.toString(prd.ID)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue()==null){
                    return Transaction.success(mutableData);

                }else {
                    String cQty = mutableData.getValue().toString();
                    int cnum = Integer.parseInt(cQty);
                    int amount = prd.Qty;
                    int leftNum = cnum - amount;
                    if(leftNum>=0){
                        mutableData.setValue(Integer.toString(leftNum));
                        return Transaction.success(mutableData);
                    }else {
                        return Transaction.abort();
                    }
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError!=null){
                    //Transaction Error
                    makeSimpleAlert("UNKNOWN ERROR", "Please Try Again.");
                    return;
                }else{
                    //aobort or success
                    if(b){
                        //write to orders folder and remove from cart folder
                        OrderPrd oprd = new OrderPrd();
                        oprd.Qty = prd.Qty;
                        oprd.cs = prd.prdCS;
                        oprd.date = Tools.getDateOnly();
                        oprd.prdKey = prd.prdKey;
                        oprd.price = prd.prdPrice;
                        oprd.selectedCSID = prd.ID;
                        oprd.status = 0;
                        oprd.time = Tools.getTimeOnly();
                        oprd.title = prd.prdTitle;
                        oprd.url = prd.prdImg;
                        oprd.userKey = UID;
                        String orderKey = addressRef.push().getKey();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/PUBLICORDERS/" + oprd.date + "/" + orderKey, oprd);
                        childUpdates.put("/users/" + oprd.userKey + "/Orders/" + orderKey, oprd);
                        //remove this prd from cart
                        reference.child(UID).child("SHOPPINGCART").child(prd.cartKey).removeValue();
                        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    if(i==cishu){
                                        progressDialog.dismiss();
                                        if(isLowQty){
                                            //some are low in stock
                                            makeSimpleAlert("Low in Stock", "Some products are low in stock, please reduce the quantity or buy other products.");
                                        }else {
                                            //all check out
                                            makeSimpleAlert("SUCCESS","Your order will arrive within 24 Hours, Check it at ORDERS.");
                                        }
                                    }
                                }else {
                                }
                            }
                        });
                    }else {
                        //this prd is low in stocl
                        isLowQty = true;
                        if(i==cishu){
                            progressDialog.dismiss();
                            //alert low qty message
                            makeSimpleAlert("Low in Stock", "Some products are low in stock, please reduce the quantity or buy other product.");
                        }
                    }
                }

            }
        });
    }

    private void makeSimpleAlert(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(msg).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }




}
