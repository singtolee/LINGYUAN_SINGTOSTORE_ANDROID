package store.singto.singtostore.LoginRegister;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import store.singto.singtostore.R;
import store.singto.singtostore.Tools.Tools;

public class PasswordResetAvtivity extends AppCompatActivity {
    private Button dismissSelfBtn;
    private EditText emailField;
    private Button sendEmailBtn;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_avtivity);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.activity_password_reset_avtivity);

        emailField = (EditText) findViewById(R.id.userEmailtoresetPassword);
        sendEmailBtn = (Button) findViewById(R.id.sendEmailBtn);
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                if (Tools.isEmail(email)) {
                    //try to send email
                    final ProgressDialog progressDialog = new ProgressDialog(PasswordResetAvtivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(getString(R.string.sendingresetpasswordemail));
                    progressDialog.show();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                //tell user to check inbox
                                Snackbar.make(coordinatorLayout,getString(R.string.resetpasswordemailwassent), Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                }).show();
                            }else {
                                //tell what error it is
                               emailField.setError(task.getException().getLocalizedMessage());
                            }
                        }
                    });

                }else {
                    emailField.setError(getString(R.string.invalidemail));
                }

            }
        });

        dismissSelfBtn = (Button) findViewById(R.id.dismissResetPasswordPageBtn);
        dismissSelfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
