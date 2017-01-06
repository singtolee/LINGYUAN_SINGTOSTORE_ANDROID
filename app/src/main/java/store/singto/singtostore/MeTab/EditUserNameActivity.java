package store.singto.singtostore.MeTab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import store.singto.singtostore.LoginRegister.LoginActivity;
import store.singto.singtostore.R;

public class EditUserNameActivity extends AppCompatActivity {

    private Button updateUserNameBtn;
    private EditText userNameField;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_name);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(EditUserNameActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.updating));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
        updateUserNameBtn = (Button) findViewById(R.id.updateUserNameBtn);
        userNameField = (EditText) findViewById(R.id.editUserName);
        Intent i = getIntent();
        userNameField.setText(i.getStringExtra("name"));
        userNameField.setSingleLine();
        updateUserNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name = userNameField.getText().toString().trim();
                if (name.length()>0){
                    if(auth.getCurrentUser().getUid()!=null){
                        String uid = auth.getCurrentUser().getUid();
                        databaseReference.child(uid).child("USERINFO").child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    finish();
                                }else {
                                    userNameField.setError(task.getException().getLocalizedMessage());
                                }
                            }
                        });
                    }
                }else {
                    progressDialog.dismiss();
                    userNameField.setError(getString(R.string.namecannotbeempty));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
