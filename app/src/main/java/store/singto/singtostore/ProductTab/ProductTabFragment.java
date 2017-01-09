package store.singto.singtostore.ProductTab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import store.singto.singtostore.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductTabFragment extends Fragment {

    private TabLayout categoryTab;
    private ViewPager viewPager;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;

    private AVLoadingIndicatorView indicatorView;

    private List<String> categories;

    public ProductTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_tab, container, false);

        indicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.loadingIndicator);

        viewPager = (ViewPager) view.findViewById(R.id.prdViewPager);
        viewPager.setOffscreenPageLimit(3); //cache 123A321 pages
        categoryTab = (TabLayout) view.findViewById(R.id.categoryTab);
        categoryTab.setupWithViewPager(viewPager);
        reference = FirebaseDatabase.getInstance().getReference().child("ProductCategory");
        categories = new ArrayList<>();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Adapter adapter = new Adapter(getFragmentManager());
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    String cate = snap.getValue().toString();
                    categories.add(cate);
                    OneCategoryPrdFragment aa = new OneCategoryPrdFragment();
                    aa.category = cate;
                    adapter.addFragment(aa, cate);
                }
                viewPager.setAdapter(adapter);
                indicatorView.hide();
                indicatorView.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(valueEventListener);
        indicatorView.show();
        return view;
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
