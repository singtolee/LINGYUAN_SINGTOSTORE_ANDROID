package store.singto.singtostore.ProductTab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import store.singto.singtostore.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OneCategoryPrdFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OneCategoryPrdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OneCategoryPrdFragment extends Fragment {

    public String category;
    private List<ShortPrd> prds; //static will make prds shared across all instances!!!!!
    private RecyclerView recyclerView;
    private ShortPrdAdapter adapter;
    private AVLoadingIndicatorView indicatorView;

    private DatabaseReference reference;
    private ChildEventListener listener;

    public OneCategoryPrdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one_category_prd, container, false);
        indicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.shortPrdLoadingIndicator);
        reference = FirebaseDatabase.getInstance().getReference().child("Each_Category").child(category);
        prds = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.shortPrdRecycleView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(12));
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // here create a list with the name of category
                ShortPrd prd = new ShortPrd();
                prd.prdKey = dataSnapshot.getKey();
                prd.imgUrl = dataSnapshot.child("productMainImage").getValue().toString();
                prd.prdName = dataSnapshot.child("productName").getValue().toString();
                prd.prdPrice = dataSnapshot.child("productPrice").getValue().toString();
                prd.prdSub = dataSnapshot.child("productSubDetail").getValue().toString();
                prds.add(0,prd);
                adapter = new ShortPrdAdapter(getContext(),prds);
                adapter.setOnItemClickListener(new ShortPrdAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int index) {
                        Intent intent = new Intent(getActivity(), DetailPrdActivity.class);
                        intent.putExtra("prdKey", prds.get(index).prdKey);
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
                indicatorView.hide();
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
                indicatorView.hide();

            }
        };
        loadShortPrds();
        return view;
    }

    private void loadShortPrds(){
        indicatorView.show();
        reference.addChildEventListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, sub, price;
        public ImageView mainImg;
        public ViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.shortprdcell_cardview, viewGroup, false));
            name = (TextView) itemView.findViewById(R.id.prdName);
            name.setMaxLines(1);
            name.setEllipsize(TextUtils.TruncateAt.END);
            sub = (TextView) itemView.findViewById(R.id.prdSub);
            sub.setMaxLines(2);
            sub.setEllipsize(TextUtils.TruncateAt.END);
            price = (TextView) itemView.findViewById(R.id.prdPrice);
            mainImg = (ImageView) itemView.findViewById(R.id.mainPrdImage);
        }
    }

    public static class ShortPrdAdapter extends RecyclerView.Adapter<ViewHolder>{
        private Context context;
        private List<ShortPrd> list;

        public ShortPrdAdapter(Context context, List<ShortPrd> list){
            this.context = context;
            this.list = list;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ShortPrd p = list.get(position);
            holder.name.setText(p.prdName);
            holder.sub.setText(p.prdSub);
            holder.price.setText("THB "+p.prdPrice+".0");
            Picasso.with(context).load(p.imgUrl).placeholder(R.drawable.placeholder).into(holder.mainImg);

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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
            outRect.left = space;
            outRect.right = space/2;
            outRect.bottom = space/2;
        }
    }
}
