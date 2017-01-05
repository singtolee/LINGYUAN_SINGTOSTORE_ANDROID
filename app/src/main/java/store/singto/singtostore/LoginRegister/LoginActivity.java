package store.singto.singtostore.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import store.singto.singtostore.R;
import store.singto.singtostore.Tools.Tools;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button returntoMain;
    private Button gotoRegister;
    private Button signinWithEmailandPassword;
    private Button facebookBtn;
    private Button goTOResetPasswordActivity;

    private DatabaseReference mDatabase;

    private EditText emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        emailField = (EditText) findViewById(R.id.userEmail);
        passwordField = (EditText) findViewById(R.id.userPassword);

        goTOResetPasswordActivity = (Button) findViewById(R.id.forgetPasswordBtn);
        goTOResetPasswordActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PasswordResetAvtivity.class);
                startActivity(intent);
            }
        });

        returntoMain = (Button) findViewById(R.id.returntoMain);
        gotoRegister = (Button) findViewById(R.id.gotoRegisterBtn);
        facebookBtn = (Button) findViewById(R.id.loginwithfacebookBtn);
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //emailField.setError("IAM OK");
                //mDatabase.child("TEST").setValue(1234);
            }
        });
        signinWithEmailandPassword = (Button) findViewById(R.id.loginWithEmailBtn);

        signinWithEmailandPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.loggingin));
                progressDialog.show();
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if(Tools.isEmail(email) && !password.isEmpty()) {
                    //ready to sign in
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                finish();
                            }else {
                                progressDialog.dismiss();
                                String err = task.getException().getLocalizedMessage();
                                Toast.makeText(LoginActivity.this, err, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    if (!Tools.isEmail(email)) {
                        emailField.setError(getString(R.string.invalidemail));
                        emailField.requestFocus();
                    }
                    if (password.isEmpty()) {
                        passwordField.setError(getString(R.string.passwordcannotbeempty));
                        passwordField.requestFocus();
                    }
                }
            }
        });

        returntoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
