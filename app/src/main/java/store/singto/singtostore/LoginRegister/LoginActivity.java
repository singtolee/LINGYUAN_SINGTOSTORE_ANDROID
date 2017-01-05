package store.singto.singtostore.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

import store.singto.singtostore.R;
import store.singto.singtostore.Tools.Tools;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private LoginManager loginManager;
    private CallbackManager callbackManager;

    private ProgressDialog progressDialog;

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
        FacebookSdk.sdkInitialize(getApplicationContext());

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loggingin));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // check if first time sign in, if yes, save user info into database
                exchangeCredential(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                // do nothing
                Toast.makeText(LoginActivity.this, "CANCEL ERROR", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }

            @Override
            public void onError(FacebookException error) {
                //handle error
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });

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
                progressDialog.show();
                loginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            }
        });
        signinWithEmailandPassword = (Button) findViewById(R.id.loginWithEmailBtn);

        signinWithEmailandPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void exchangeCredential(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    progressDialog.dismiss();
                    //error
                }else {
                    //success, check is user info exists, if no, save info
                    final FirebaseUser user = mAuth.getCurrentUser();
                    ValueEventListener listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null) {
                                //already exist
                                progressDialog.dismiss();
                                finish();
                            }else {
                                //user info is empty,set value
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                String url = user.getPhotoUrl().toString();
                                HashMap<String, String> info = new HashMap<>();
                                if(name!=null){
                                    info.put("name",name);
                                }
                                if(email!=null){
                                    info.put("email",email);
                                }
                                if(url!=null){
                                    info.put("userAvatarUrl",url);
                                }
                                mDatabase.child("users").child(user.getUid()).child("USERINFO").setValue(info);
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    mDatabase.child("users").child(user.getUid()).child("USERINFO").addListenerForSingleValueEvent(listener);
                }
            }
        });

    }
}
