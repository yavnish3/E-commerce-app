package firebase.app.mall24x7.Buyer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import firebase.app.mall24x7.Prevalent.Prevalent;
import firebase.app.mall24x7.R;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullnameEdittxt,userPhoneEditTxt,addressEditTxt;
    private TextView profilechangeBtn,closeTextBtn,saveTxtBtn;
    private Button securityBtn;

    private Uri imageUri;
    private String myUrl="",checker="";
    StorageTask uploadTask;
    private StorageReference storageProfileReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        storageProfileReference= FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView=(CircleImageView)findViewById(R.id.setting_profile_image);
        fullnameEdittxt=(EditText) findViewById(R.id.setting_full_name);
        userPhoneEditTxt=(EditText) findViewById(R.id.setting_phone_number);
        addressEditTxt=(EditText) findViewById(R.id.setting_address);
        profilechangeBtn=(TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn=(TextView) findViewById(R.id.close_setting_btn);
        saveTxtBtn=(TextView) findViewById(R.id.update_setting_btn);
        securityBtn=findViewById(R.id.security_questions_btn);

        userInfoDisplay(profileImageView,fullnameEdittxt,userPhoneEditTxt,addressEditTxt);


        securityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 Intent intent=new Intent(SettingActivity.this, ResetPasswordActivity.class);
                 intent.putExtra("check","settings");
                 startActivity(intent);
            }
        });

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked"))
                {
                    userInfoSave();

                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });


        profilechangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingActivity.this);

            }
        });
    }

    private void updateOnlyUserInfo() {


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",fullnameEdittxt.getText().toString());
        userMap.put("address",addressEditTxt.getText().toString());
        userMap.put("phoneOrder",userPhoneEditTxt.getText().toString());
        ref.child(Prevalent.currentUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingActivity.this, HomeActivity.class));

        Toast.makeText(SettingActivity.this, "Profile Info Updates Successfully", Toast.LENGTH_SHORT).show();
        finish();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!= null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            profileImageView.setImageURI(imageUri);

        }
        else
        {
            Toast.makeText(this, "Error, Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingActivity.this,SettingActivity.class));
            finish();
        }
    }

    private void userInfoSave() {

        if (TextUtils.isEmpty(fullnameEdittxt.getText().toString()))
        {
            Toast.makeText(this, "Name is Mandatory", Toast.LENGTH_SHORT).show();
        }

        else   if (TextUtils.isEmpty(addressEditTxt.getText().toString()))
        {
            Toast.makeText(this, "Address is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else   if (TextUtils.isEmpty(userPhoneEditTxt.getText().toString()))
        {
            Toast.makeText(this, "Phone is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadImage();
        }



    }

    private void uploadImage() {

        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Upload Profile");
        progressDialog.setMessage("Please wait while updating account Information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri!=null)
        {
            final StorageReference fileRef=storageProfileReference
                    .child(Prevalent.currentUser.getPhone() +".jpg");

            uploadTask=fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri dowanloadUri=task.getResult();
                        myUrl=dowanloadUri.toString();

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("name",fullnameEdittxt.getText().toString());
                        userMap.put("address",addressEditTxt.getText().toString());
                        userMap.put("phoneOrder",userPhoneEditTxt.getText().toString());
                        userMap.put("image",myUrl);

                        ref.child(Prevalent.currentUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingActivity.this,HomeActivity.class));

                        Toast.makeText(SettingActivity.this, "Profile Info Updates Successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                    }


                }
            });

        }
        else
        {
            Toast.makeText(this, "Image is not Selected", Toast.LENGTH_SHORT).show();
        }

    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullnameEdittxt, final EditText userPhoneEditTxt, final EditText addressEditTxt)
    {

        DatabaseReference UserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentUser.getPhone());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {

                    if (dataSnapshot.child("image").exists())
                    {
                        String image=dataSnapshot.child("image").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        String phone=dataSnapshot.child("phone").getValue().toString();
                        String address=dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnameEdittxt.setText(name);
                        userPhoneEditTxt.setText(phone);
                        addressEditTxt.setText(address);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
