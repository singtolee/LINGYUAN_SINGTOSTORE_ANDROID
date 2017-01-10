package store.singto.singtostore.ProductTab;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import store.singto.singtostore.R;

public class DetailPrdActivity extends AppCompatActivity {
    private String prdKey;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private ValueEventListener listener;
    private SliderLayout carousel;
    private Point size;
    private int ssw,ssh;
    private TextView title, price, sub;
    private LinearLayout commitmentsPart;
    private TextView freeshipping, shippingTime,cod,refundable, nonrefundable;

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
        reference = FirebaseDatabase.getInstance().getReference().child("AllProduct").child(prdKey);
        auth = FirebaseAuth.getInstance();
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


        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    DetailPrd prd = new DetailPrd();
                    //prd = null;
                    prd.prdName = dataSnapshot.child("productName").getValue().toString();
                    prd.prdSub = dataSnapshot.child("productSubDetail").getValue().toString();
                    prd.prdPrice = dataSnapshot.child("productPrice").getValue().toString();
                    prd.prdPackageInfo = dataSnapshot.child("productPackageInfo").getValue().toString();
                    prd.prdSuppler = dataSnapshot.child("productSuppler").getValue().toString();
                    prd.prdRefundable = (Boolean) dataSnapshot.child("productRefundable").getValue();
                    if(prd.prdImages!=null){
                        prd.prdImages.clear();
                    }
                    prd.prdImages = (List<String>) dataSnapshot.child("productImages").getValue();
                    if(dataSnapshot.hasChild("productInfoImages")){
                        if(prd.prdInfoImages!=null){
                            prd.prdInfoImages.clear();
                        }
                        prd.prdInfoImages = (List<String>) dataSnapshot.child("productInfoImages").getValue();
                    }
                    if(prd.prdCS!=null){
                        prd.prdCS.clear();
                    }
                    prd.prdCS = (List<String>) dataSnapshot.child("prodcutCS").getValue();
                    if(prd.prdCSQty!=null){
                        prd.prdCSQty.clear();
                    }
                    prd.prdCSQty = (List<String>) dataSnapshot.child("prodcutCSQty").getValue();
                    updateUI(prd);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(listener);


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
        //reference.addValueEventListener(listener);
        //carousel.stopAutoCycle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        carousel.stopAutoCycle();
        //reference.removeEventListener(listener);
    }

    private void updateUI(DetailPrd prd){
        ViewGroup.LayoutParams params = carousel.getLayoutParams();
        params.height = ssw;
        params.width = ssw;
        carousel.setLayoutParams(params);
        for(String url: prd.prdImages){
            DefaultSliderView c = new DefaultSliderView(this);
            c.image(url);
            carousel.addSlider(c);
        }

        title.setText(prd.prdName);
        price.setText("THB "+prd.prdPrice+".0");
        sub.setText(prd.prdSub);

        commitmentsPart.setVisibility(View.VISIBLE);

        final float density = getResources().getDisplayMetrics().density;
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


    }
}
