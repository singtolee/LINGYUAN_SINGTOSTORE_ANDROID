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

import store.singto.singtostore.R;

public class EditUserPhoneActivity extends AppCompatActivity {
    private Button updateUserPhoneBtn;
    private EditText userPhoneField;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_phone);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(EditUserPhoneActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.updating));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
        updateUserPhoneBtn = (Button) findViewById(R.id.updateUserPhoneBtn);
        userPhoneField = (EditText) findViewById(R.id.editUserPhone);
        userPhoneField.setSingleLine();
        Intent i = getIntent();
        userPhoneField.setText(i.getStringExtra("phone"));
        updateUserPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String phone = userPhoneField.getText().toString().trim();
                if (phone.length()==10){
                    if(auth.getCurrentUser().getUid()!=null){
                        String uid = auth.getCurrentUser().getUid();
                        databaseReference.child(uid).child("USERINFO").child("phone").setValue(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    finish();
                                }else {
                                    userPhoneField.setError(task.getException().getLocalizedMessage());
                                }
                            }
                        });
                    }
                }else {
                    progressDialog.dismiss();
                    userPhoneField.setError(getString(R.string.phonenumberistendigits));
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
