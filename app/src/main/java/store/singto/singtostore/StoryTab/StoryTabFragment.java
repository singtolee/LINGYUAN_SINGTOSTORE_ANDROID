package store.singto.singtostore.StoryTab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import store.singto.singtostore.R;
import store.singto.singtostore.Tools.Tools;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoryTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoryTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryTabFragment extends Fragment {
    private DatabaseReference ref;
    private ChildEventListener listener;
    private List<PopularPrd> popularPrds;
    private RecyclerView popularRV;
    private PopularAdapter adapter;
    private Query query;

    public StoryTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story_tab, container, false);
        ref = FirebaseDatabase.getInstance().getReference().child("AllProduct");
        query = ref.orderByChild("viewCount");
        popularPrds = new ArrayList<>();
        popularRV = (RecyclerView) view.findViewById(R.id.popularPrdRecyclerView);
        popularRV.addItemDecoration(new Tools.RecyPadding(0,0,4,0));
        popularRV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PopularAdapter(getContext(),popularPrds);
        popularRV.setAdapter(adapter);
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PopularPrd p = new PopularPrd();
                p.prdKey = dataSnapshot.getKey();
                List<String> URLS = (List<String>) dataSnapshot.child("productImages").getValue();
                p.url = URLS.get(0);
                popularPrds.add(p);
                adapter.notifyItemInserted(popularPrds.size()-1);
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
        return view;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView img;

        public ViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup){
            super(layoutInflater.inflate(R.layout.popularprd_cardview, viewGroup, false));
            img = (ImageView) itemView.findViewById(R.id.popularPrdImg);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public static class PopularAdapter extends RecyclerView.Adapter<ViewHolder>{
        private Context context;
        private List<PopularPrd> list;
        public PopularAdapter(Context context, List<PopularPrd> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final PopularPrd pprd = list.get(position);
            Picasso.with(context).load(pprd.url).into(holder.img);
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

    @Override
    public void onStart() {
        super.onStart();
        popularPrds.clear();
        adapter.notifyDataSetChanged();
        //Query query = ref.orderByChild("viewCount");
        query.addChildEventListener(listener);
        //ref.addChildEventListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //ref.removeEventListener(listener);
        query.removeEventListener(listener);
        popularPrds.clear();
        adapter.notifyDataSetChanged();
    }
}
