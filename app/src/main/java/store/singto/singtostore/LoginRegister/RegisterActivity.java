package store.singto.singtostore.LoginRegister;

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
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                if(isEmail(email) && password.length()>5) {
                    //ready to register
                    //Toast.makeText(RegisterActivity.this, "GGOOOODDD", Toast.LENGTH_LONG).show();
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "ERROR ON REGISTER", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(RegisterActivity.this, "SUCCESSFUL", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    if (!isEmail(email)){
                        emailField.setError("Invalid Email Address");
                    }
                    if (password.length() <= 5){
                        passwordField.setError("Password should longer than 5 digits");
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

    private boolean isEmail(String email){
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }else {
            return true;
        }
    }
}
