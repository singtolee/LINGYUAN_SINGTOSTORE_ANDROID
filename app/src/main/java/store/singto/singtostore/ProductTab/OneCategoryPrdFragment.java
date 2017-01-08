package store.singto.singtostore.ProductTab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public OneCategoryPrdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one_category_prd, container, false);
    }

}
