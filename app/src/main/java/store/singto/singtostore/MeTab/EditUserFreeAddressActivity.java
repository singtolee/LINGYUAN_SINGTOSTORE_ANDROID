package store.singto.singtostore.MeTab;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import store.singto.singtostore.R;

public class EditUserFreeAddressActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference reference,locRef;
    private ValueEventListener listener;
    private ChildEventListener locListener;
    private FreeAddress address;
    private EditText name, phone, room;
    private Button saveAddressBtn;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private List<String> locations;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_free_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.updating));
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("FreeDeliveryAddresses");
        locRef = FirebaseDatabase.getInstance().getReference().child("OfficeBuildings");
        address = new FreeAddress();
        locations = new ArrayList<>();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    address = dataSnapshot.getValue(FreeAddress.class);
                    name.setText(address.recipient);
                    phone.setText(address.phone);
                    room.setText(address.roomNumber);
                    for(int i=0;i<locations.size();i++){
                        if(address.officeBuilding.equals(locations.get(i))){
                            spinner.setSelection(i);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        locListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String villa = dataSnapshot.getValue().toString();
                locations.add(villa);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        name = (EditText)findViewById(R.id.addressUserName);
        name.setSingleLine();
        phone = (EditText)findViewById(R.id.addressUserPhone);
        room = (EditText)findViewById(R.id.addressUserRoom);
        room.setSingleLine();
        saveAddressBtn = (Button)findViewById(R.id.submitFreeAddress);
        saveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String na = name.getText().toString().trim();
                String ph = phone.getText().toString().trim();
                String ob = spinner.getSelectedItem().toString();
                String ro = room.getText().toString().trim();
                if(na.length()>0&&ph.length()==10&&ro.length()>0){
                    String uid = auth.getCurrentUser().getUid();
                    FreeAddress dizhi = new FreeAddress();
                    dizhi.recipient = na;
                    dizhi.phone = ph;
                    dizhi.officeBuilding = ob;
                    dizhi.roomNumber = ro;
                    reference.child(uid).setValue(dizhi).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(EditUserFreeAddressActivity.this,R.string.addressSaved,Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(EditUserFreeAddressActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }else {
                    progressDialog.dismiss();
                    if(na.isEmpty()){
                        name.setError(getString(R.string.namecannotbeempty));
                    }
                    if(ph.length()!=10){
                        phone.setError(getString(R.string.phonenumberistendigits));
                    }
                    if(ro.isEmpty()){
                        room.setError(getString(R.string.roomCannotempty));
                    }

                }
            }
        });
        spinner = (Spinner)findViewById(R.id.locationSpinner);
        spinner.setPrompt(getString(R.string.buildingHint));
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,locations);
        spinner.setAdapter(adapter);

        getUserAddress();
        loadLocations();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserAddress(){
        if(auth.getCurrentUser()!=null){
            String uid = auth.getCurrentUser().getUid();
            reference.child(uid).addListenerForSingleValueEvent(listener);
        }
    }

    private void loadLocations(){
        locRef.addChildEventListener(locListener);
    }
}
