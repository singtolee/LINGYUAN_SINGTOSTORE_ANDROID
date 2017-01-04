package store.singto.singtostore.MeTab;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import store.singto.singtostore.LoginRegister.LoginActivity;
import store.singto.singtostore.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeTabFragment extends Fragment {
    private Button loginRegisterBtn, exitAppBtn;
    private ImageView userAvatarImgv;
    private TextView userName;

    public MeTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_tab, container, false);
        userAvatarImgv = (ImageView) view.findViewById(R.id.userAvatar);
        userName = (TextView) view.findViewById(R.id.userName);
        loginRegisterBtn = (Button) view.findViewById(R.id.loginRegisterBtn);
        exitAppBtn = (Button) view.findViewById(R.id.exitAppBtn);

        exitAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout , clean userAvatar, name, hide exitAppBtn, hide userName
            }
        });

        loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to login page
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
