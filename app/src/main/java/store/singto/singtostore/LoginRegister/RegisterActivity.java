package store.singto.singtostore.LoginRegister;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import store.singto.singtostore.R;
import store.singto.singtostore.Tools.Tools;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button returnLogin, registerBtn;
    private EditText emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailField = (EditText) findViewById(R.id.userEmailforRegister);
        passwordField = (EditText) findViewById(R.id.userPasswordforRegister);

        returnLogin = (Button) findViewById(R.id.returntoLogin);
        registerBtn = (Button) findViewById(R.id.registerWithEmailBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.registering));
                progressDialog.show();
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                if(Tools.isEmail(email) && password.length()>5) {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(!task.isSuccessful()){
                                emailField.setError(task.getException().getLocalizedMessage());
                            }else {
                                finish();
                            }
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    if (!Tools.isEmail(email)){
                        emailField.setError(getString(R.string.invalidemail));
                    }
                    if (password.length() <= 5){
                        passwordField.setError(getString(R.string.passwordtooshort));
                    }
                }


            }
        });

        returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
