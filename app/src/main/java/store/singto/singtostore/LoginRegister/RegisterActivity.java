package store.singto.singtostore.LoginRegister;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import store.singto.singtostore.R;

public class RegisterActivity extends AppCompatActivity {
    private Button returnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        returnLogin = (Button) findViewById(R.id.returntoLogin);

        returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
