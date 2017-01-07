package store.singto.singtostore.MeTab;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;

import store.singto.singtostore.R;

public class EditUserAvatarActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ImageView largeAvatar;
    private ValueEventListener listener;
    private Button uploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_avatar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uploadBtn = (Button)findViewById(R.id.uploadAvatar);
        uploadBtn.setVisibility(View.INVISIBLE);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg();
            }
        });
        largeAvatar = (ImageView)findViewById(R.id.largeAvatar);
        largeAvatar.setScaleType(ImageView.ScaleType.FIT_XY);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                   String url = dataSnapshot.getValue().toString();
                   Picasso.with(EditUserAvatarActivity.this).load(url).into(largeAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        loadAvatar();
    }

    private void loadAvatar(){
        if(auth.getCurrentUser().getUid()!=null){
            String uid = auth.getCurrentUser().getUid();
            databaseReference.child(uid).child("USERINFO").child("userAvatarUrl").addListenerForSingleValueEvent(listener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opengalleryicon, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        if(item.getItemId() == R.id.opengallery){
            largeAvatar.setImageDrawable(null);
            Crop.pickImage(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Crop.REQUEST_PICK){
            beginCrop(data.getData());
        }else if(requestCode == Crop.REQUEST_CROP){
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            largeAvatar.setImageURI(Crop.getOutput(result));
            uploadBtn.setVisibility(View.VISIBLE);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImg(){
        uploadBtn.setVisibility(View.INVISIBLE);
    }
}
