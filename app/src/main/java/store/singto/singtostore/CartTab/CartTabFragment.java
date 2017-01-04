package store.singto.singtostore.CartTab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public CartTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart_tab, container, false);
    }
}
