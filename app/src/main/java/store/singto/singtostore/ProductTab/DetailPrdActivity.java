package store.singto.singtostore.ProductTab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;

import store.singto.singtostore.LoginRegister.LoginActivity;
import store.singto.singtostore.R;
import store.singto.singtostore.Tools.MyGridView;

public class DetailPrdActivity extends AppCompatActivity {
    private String prdKey;
    private DetailPrd product;
    private int csID = -1;

    private DatabaseReference reference, ref;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ValueEventListener listener;

    private SliderLayout carousel;
    private Point size;
    private int ssw,ssh;

    private TextView title, price, sub;
    private LinearLayout commitmentsPart;
    private TextView freeshipping, shippingTime,cod,refundable, nonrefundable;

    private MyGridView prdCSListView, prdInfoImgGridView;
    private View endView;

    private ImageView likeBtn;
    private Button cartBtn, buyBtn;

    //variables for gridview
    TextView GridViewItems, BackSelectedItem, tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_prd);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WindowManager vm = getWindowManager();
        Display d = vm.getDefaultDisplay();
        size = new Point();
        d.getRealSize(size);
        ssw = size.x;
        ssh = size.y;
        Intent i = getIntent();
        prdKey = i.getStringExtra("prdKey");
        setUpUI();
        reference = FirebaseDatabase.getInstance().getReference().child("AllProduct").child(prdKey);
        ref = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    String uid = user.getUid();
                    ref.child(uid).child("FavoritePRD").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(prdKey)){
                                //set like btn
                                likeBtn.setImageResource(R.drawable.ic_favorite_white_24dp);

                            }else {
                                //set normal btn
                                likeBtn.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    DetailPrd prd = new DetailPrd();
                    //prd.reset();
                    prd.prdName = dataSnapshot.child("productName").getValue().toString();
                    prd.prdSub = dataSnapshot.child("productSubDetail").getValue().toString();
                    prd.prdPrice = dataSnapshot.child("productPrice").getValue().toString();
                    prd.prdPackageInfo = dataSnapshot.child("productPackageInfo").getValue().toString();
                    prd.prdSuppler = dataSnapshot.child("productSuppler").getValue().toString();
                    prd.prdRefundable = (Boolean) dataSnapshot.child("productRefundable").getValue();
                    prd.prdImages = (List<String>) dataSnapshot.child("productImages").getValue();
                    prd.prdInfoImages = (List<String>) dataSnapshot.child("productInfoImages").getValue();
                    prd.prdCS = (List<String>) dataSnapshot.child("prodcutCS").getValue();
                    prd.prdCSQty = (List<String>) dataSnapshot.child("prodcutCSQty").getValue();
                    product = new DetailPrd();
                    product = prd;
                    updateUI(prd);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        //load prd by prdkey
        //reference.addValueEventListener(listener);
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
        reference.addValueEventListener(listener);
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(listener);
        if(authStateListener!=null){
            auth.removeAuthStateListener(authStateListener);
        }
    }

    private void updateUI(final DetailPrd prd){
        ViewGroup.LayoutParams params = carousel.getLayoutParams();
        params.height = ssw;
        params.width = ssw;
        carousel.setLayoutParams(params);
        carousel.removeAllSliders();
        for(String url: prd.prdImages){
            DefaultSliderView c = new DefaultSliderView(this);
            c.image(url);
            carousel.addSlider(c);
        }
        carousel.stopAutoCycle();

        title.setText(prd.prdName);
        price.setText("THB "+prd.prdPrice+".0");
        sub.setText(prd.prdSub);

        commitmentsPart.setVisibility(View.VISIBLE);

        final float density = getResources().getDisplayMetrics().density;
        //final Drawable drawable = getDrawable(R.drawable.dot);
        final Drawable drawable = getDrawable(R.drawable.dot);
        final int height = Math.round(8 * density);

        drawable.setBounds(0, -4, height, height-4);
        freeshipping.setCompoundDrawables(drawable, null, null, null);
        shippingTime.setCompoundDrawables(drawable,null,null,null);
        cod.setCompoundDrawables(drawable,null,null,null);

        if(prd.prdRefundable){
            refundable.setCompoundDrawables(drawable,null,null,null);
            nonrefundable.setVisibility(View.GONE);
        }else {
            final Drawable undot = getDrawable(R.drawable.undot);
            undot.setBounds(0,-4,height,height-4);
            nonrefundable.setCompoundDrawables(undot,null,null,null);
            refundable.setVisibility(View.GONE);
        }

        prdCSListView.setAdapter(new CSAdapter(this, prd.prdCS, prd.prdCSQty));
        //check Qty, ViewTreeObserver make sure gridview.getChild return non null obj
        ViewTreeObserver vto = prdCSListView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for(int i=0;i<prd.prdCSQty.size();i++){
                    tv = (TextView) prdCSListView.getChildAt(i);
                    tv.setBackgroundColor(getColor(R.color.whiteColor));
                    tv.setTextColor(getColor(R.color.colorPrimary));
                }
                for(int i=0;i<prd.prdCSQty.size();i++){
                    int cqty = Integer.parseInt(prd.prdCSQty.get(i));
                    if(cqty>0){
                        tv = (TextView) prdCSListView.getChildAt(i);
                        tv.setBackgroundColor(getColor(R.color.colorPrimary));
                        tv.setTextColor(getColor(R.color.whiteColor));
                        csID = i;
                        cartBtn.setEnabled(true);
                        buyBtn.setEnabled(true);
                        break;
                    }
                }
            }
        });
        prdCSListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //selectedItem = parent.getItemAtPosition(position).toString();
                //de select all
                for(int i=0;i<prd.prdCS.size();i++){
                    BackSelectedItem = (TextView) prdCSListView.getChildAt(i);
                    BackSelectedItem.setBackgroundColor(getColor(R.color.whiteColor));
                    BackSelectedItem.setTextColor(getColor(R.color.colorPrimary));
                }
                //change color at position
                GridViewItems = (TextView) view;
                GridViewItems.setBackgroundColor(getColor(R.color.colorPrimary));
                GridViewItems.setTextColor(getColor(R.color.whiteColor));
                csID = position;
            }
        });

        if(prd.prdInfoImages!=null){
            prdInfoImgGridView.setAdapter(new InfoAdapter(this,prd.prdInfoImages));
        }else {
            prdInfoImgGridView.setVisibility(View.GONE);
        }

        likeBtn.setVisibility(View.VISIBLE);
        cartBtn.setVisibility(View.VISIBLE);
        buyBtn.setVisibility(View.VISIBLE);
    }

    private void setUpUI(){
        carousel = (SliderLayout) findViewById(R.id.prdImgsCarousel);
        carousel.setCustomIndicator((PagerIndicator) findViewById(R.id.carouselPagerIndicator));

        title = (TextView)findViewById(R.id.detailPrdName);
        price = (TextView)findViewById(R.id.detailPrdPricce);
        sub = (TextView)findViewById(R.id.detailPrdSub);
        sub.setEllipsize(TextUtils.TruncateAt.END);

        commitmentsPart = (LinearLayout)findViewById(R.id.commintmentspart);
        commitmentsPart.setVisibility(View.INVISIBLE);

        freeshipping = (TextView)findViewById(R.id.freeshipping);
        shippingTime = (TextView)findViewById(R.id.shippingfast);
        cod = (TextView)findViewById(R.id.cod);
        refundable = (TextView)findViewById(R.id.refundable);
        nonrefundable = (TextView)findViewById(R.id.nonRefundable);
        prdCSListView = (MyGridView) findViewById(R.id.prdCSListView);
        prdInfoImgGridView = (MyGridView) findViewById(R.id.prdInfoImgs);

        endView = findViewById(R.id.thisisendview);

        likeBtn = (ImageView) findViewById(R.id.likeBtn);
        likeBtn.setVisibility(View.INVISIBLE);
        cartBtn = (Button)findViewById(R.id.addToCartBtn);
        cartBtn.setVisibility(View.INVISIBLE);
        cartBtn.setEnabled(false);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser()!=null){
                    String uid = auth.getCurrentUser().getUid();
                    //add to cart
                    CartPrd cart = new CartPrd();
                    cart.ID = csID;
                    cart.Check = true;
                    cart.Qty = 1;
                    cart.prdCS = product.prdCS.get(csID);
                    cart.prdImg = product.prdImages.get(csID);
                    cart.prdKey = prdKey;
                    cart.prdTitle = product.prdName;
                    cart.prdPrice = Integer.parseInt(product.prdPrice);
                    String kk = ref.child(uid).child("SHOPPINGCART").push().getKey();
                    cart.cartKey = kk;
                    ref.child(uid).child("SHOPPINGCART").child(kk).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                cartToast();
                            }else {
                                //try again
                                Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else {
                    gotosignin();
                }

            }
        });
        buyBtn = (Button)findViewById(R.id.buyNowBtn);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser()!=null){
                    //
                    System.out.println(csID);
                }else {
                    gotosignin();
                }
            }
        });
        buyBtn.setVisibility(View.INVISIBLE);
        buyBtn.setEnabled(false);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser()!=null){
                    //add this prd to favorite folder
                    final String uid = auth.getCurrentUser().getUid();
                    ref.child(uid).child("FavoritePRD").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(prdKey)){
                                ref.child(uid).child("FavoritePRD").child(prdKey).setValue(null);
                                makeToast("REMOVED");
                            }else {
                                ref.child(uid).child("FavoritePRD").child(prdKey).setValue(true);
                                makeToast("LIKED");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    //tell user to login
                    gotosignin();
                }
            }
        });

    }
    public class InfoAdapter extends BaseAdapter{
        private Context context;
        private List<String> infos;

        public InfoAdapter(Context context, List<String> infos){
            this.context = context;
            this.infos = infos;
        }

        public int getCount(){
            return infos.size();
        }

        public Object getItem(int position){
            return infos.get(position);
        }
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView csView;
            if (convertView == null) {
                csView = new ImageView(context);
                csView.setLayoutParams(new GridView.LayoutParams(ssw,ssw));
                csView.setScaleType(ImageView.ScaleType.FIT_XY);
                Picasso.with(DetailPrdActivity.this).load(infos.get(position).toString()).placeholder(R.drawable.placeholder).into(csView);
            } else {
                csView = (ImageView) convertView;
            }
            return csView;
        }


    }

    public class CSAdapter extends BaseAdapter{
        private Context context;
        private List<String> cs, csqty;

        public CSAdapter(Context context, List<String> cs, List<String> csqty){
            this.context = context;
            this.cs = cs;
            this.csqty = csqty;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            //return super.isEnabled(position);
            if(Integer.parseInt(csqty.get(position))>0){
                return true;
            }else {
                return false;
            }
        }

        public int getCount(){
            return cs.size();
        }

        public Object getItem(int position) {
            return cs.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView csView;
            if (convertView == null) {
                csView = new TextView(context);
                csView.setLayoutParams(new GridView.LayoutParams(ssw/2,ssw/17));
                csView.setEllipsize(TextUtils.TruncateAt.END);
                csView.setTextSize(ssw/88);
                //csView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                csView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                csView.setText(cs.get(position)+" ("+ csqty.get(position)+ " LEFT)");
                csView.setTextColor(getColor(R.color.colorPrimary));
                csView.setBackgroundColor(getColor(R.color.whiteColor));
            } else {
                csView = (TextView) convertView;
            }
            return csView;
        }


    }

    private void makeToast(String a){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.like_toast,(ViewGroup) findViewById(R.id.likeToastContainer));
        TextView textView = (TextView)layout.findViewById(R.id.likeordislike);
        textView.setText(a);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, -ssw/3, ssw/2);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void cartToast(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.addedtocart_toast,(ViewGroup) findViewById(R.id.addedtocarttoastcontainer));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, ssw/2);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void gotosignin(){
        Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

}

