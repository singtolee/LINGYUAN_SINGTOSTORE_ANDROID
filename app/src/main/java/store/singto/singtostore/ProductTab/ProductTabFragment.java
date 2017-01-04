package store.singto.singtostore.ProductTab;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public ProductTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_tab, container, false);
    }
}
