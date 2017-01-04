package store.singto.singtostore.LoginRegister;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import store.singto.singtostore.R;

public class LoginActivity extends AppCompatActivity {

    private Button returntoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        returntoMain = (Button) findViewById(R.id.returntoMain);

        returntoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
