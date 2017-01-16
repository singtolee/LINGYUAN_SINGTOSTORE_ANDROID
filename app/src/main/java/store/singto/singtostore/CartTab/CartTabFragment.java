package store.singto.singtostore.CartTab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import store.singto.singtostore.ProductTab.CartPrd;
import store.singto.singtostore.R;

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
    private DatabaseReference reference;
    private ChildEventListener listener;
    private List<CartPrd> carts;
    private RecyclerView cartRV;
    private CartAdapter adapter;

    public CartTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart_tab, container, false);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        carts = new ArrayList<>();
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CartPrd c = dataSnapshot.getValue(CartPrd.class);
                carts.add(c);
                adapter = new CartAdapter(getContext(), carts);
                cartRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();

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
        cartRV = (RecyclerView) view.findViewById(R.id.cartRV);
        cartRV.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //check user login or not, download carts
        if(auth.getCurrentUser()!=null){
            String uid = auth.getCurrentUser().getUid();
            reference.child(uid).child("SHOPPINGCART").addChildEventListener(listener);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        //detatch listener
        if(!carts.isEmpty()){
            carts.clear();
            adapter.notifyDataSetChanged();
        }
        if(auth.getCurrentUser()!=null){
            String uid = auth.getCurrentUser().getUid();
            reference.child(uid).child("SHOPPINGCART").removeEventListener(listener);
        }
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{
        public ImageView check, img;
        public TextView cartTitle, cartCS, cartPrice, qty, minusBtn, plusBtn;
        public CartViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.cart_cell, viewGroup,false));
            check = (ImageView) itemView.findViewById(R.id.isChecked);
            img = (ImageView) itemView.findViewById(R.id.cartMainImg);
            cartTitle = (TextView) itemView.findViewById(R.id.cartPrdName);
            cartCS = (TextView) itemView.findViewById(R.id.cartPrdCS);
            cartPrice = (TextView) itemView.findViewById(R.id.cartPrdPrice);
            qty = (TextView) itemView.findViewById(R.id.cartQty);
            minusBtn = (TextView) itemView.findViewById(R.id.cartMinusBtn);
            plusBtn = (TextView) itemView.findViewById(R.id.cartPlusBtn);
            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cv = Integer.parseInt(qty.getText().toString());
                    cv = cv - 1;
                    if(cv<1){
                        cv = 1;
                    }
                    qty.setText(Integer.toString(cv));
                }
            });
            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cv = Integer.parseInt(qty.getText().toString());
                    cv = cv + 1;
                    if(cv> 3){
                        cv = 3; //max QTY is 3
                    }
                    qty.setText(Integer.toString(cv));
                }
            });

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
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
