package store.singto.singtostore.LoginRegister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import store.singto.singtostore.R;

public class LoginActivity extends AppCompatActivity {

    private Button returntoMain;
    private Button gotoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        returntoMain = (Button) findViewById(R.id.returntoMain);
        gotoRegister = (Button) findViewById(R.id.gotoRegisterBtn);

        returntoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
