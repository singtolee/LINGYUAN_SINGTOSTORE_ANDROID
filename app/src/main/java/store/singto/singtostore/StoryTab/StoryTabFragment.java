package store.singto.singtostore.StoryTab;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import store.singto.singtostore.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoryTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoryTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryTabFragment extends Fragment {

    public StoryTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_story_tab, container, false);
    }

}
