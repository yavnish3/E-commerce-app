package firebase.app.mall24x7.Sellers;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import firebase.app.mall24x7.R;

public class SellerAddNewProductActivity extends AppCompatActivity {

    private String categoryname,Description,Price,Pname,SavecurrentDate,SavecurrentTime;
    private Button addnewproduct;
    private ImageView Inputimage;
    private EditText Inputname, Inputdescription, Inputprice;
    private static final int galeryPick = 1;
    private Uri ImageUri;
    private String productRandomKey,dowanloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef,sellerRef;
    private ProgressDialog lodingBar;
    private String sName,sAddress,sPhone,sEmail,sId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);


        categoryname = getIntent().getExtras().get("category").toString();
        ProductImagesRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef=FirebaseDatabase.getInstance().getReference().child("Products");
        sellerRef=FirebaseDatabase.getInstance().getReference().child("Sellers");

        addnewproduct = (Button) findViewById(R.id.add_new_product);
        Inputimage = (ImageView) findViewById(R.id.select_image);
        Inputdescription = (EditText) findViewById(R.id.product_description);
        Inputname = (EditText) findViewById(R.id.product_name);
        Inputprice = (EditText) findViewById(R.id.product_price);

        lodingBar=new ProgressDialog(this);

        Inputimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpenGallery();

            }
        });

        addnewproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductdata();
            }
        });

        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            sName=dataSnapshot.child("name").getValue().toString();
                            sPhone=dataSnapshot.child("phone").getValue().toString();
                            sEmail=dataSnapshot.child("email").getValue().toString();
                            sAddress=dataSnapshot.child("address").getValue().toString();
                            sId=dataSnapshot.child("sid").getValue().toString();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }



    private void OpenGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galeryPick);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galeryPick && resultCode==RESULT_OK && data!= null)
        {

            ImageUri=data.getData();
            Inputimage.setImageURI(ImageUri);
        }
    }


    private void validateProductdata() {

        Description=Inputdescription.getText().toString();
        Price=Inputprice.getText().toString();
        Pname=Inputname.getText().toString();

        if (ImageUri==null)
        {
            Toast.makeText(this, "Product Image Is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please, write product description...", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this, "Please, write product Name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Please, write product Price...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            StorageProductInfo();
        }


    }

    private void StorageProductInfo() {



        lodingBar.setTitle("Adding New Product");
        lodingBar.setMessage("Dear Seller, Please Wait, while we are Adding....");
        lodingBar.setCanceledOnTouchOutside(false);
        lodingBar.show();



        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MM dd,YYYY");
        SavecurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        SavecurrentTime=currentTime.format(calendar.getTime());

        productRandomKey=SavecurrentDate+SavecurrentTime;

        final StorageReference filepath=ProductImagesRef.child(ImageUri.getLastPathSegment()+productRandomKey);

        final UploadTask uploadTask=filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();

                lodingBar.dismiss();
                Toast.makeText(SellerAddNewProductActivity.this, "Error:- "+message, Toast.LENGTH_SHORT).show();



            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(SellerAddNewProductActivity.this, "Image Uploded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();

                        }

                        dowanloadImageUrl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful());
                        {
                            dowanloadImageUrl=task.getResult().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "Getting Image Url Successfully...", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });



    }

    private void saveProductInfoToDatabase() {

        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",SavecurrentDate);
        productMap.put("time",SavecurrentTime);
        productMap.put("descriotion",Description);
        productMap.put("image",dowanloadImageUrl);
        productMap.put("category",categoryname);
        productMap.put("price",Price);
        productMap.put("pname",Pname);

        productMap.put("sellerName",sName);
        productMap.put("sellerAddress",sAddress);
        productMap.put("sid",sId);
        productMap.put("sellerPhone",sPhone);
        productMap.put("sellerEmail",sEmail);
        productMap.put("productState","Not Approved");


        ProductsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    Intent intent=new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
                    startActivity(intent);


                    lodingBar.dismiss();
                    Toast.makeText(SellerAddNewProductActivity.this, "Product is Added Successfully...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    lodingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(SellerAddNewProductActivity.this, "Error:- "+message, Toast.LENGTH_SHORT).show();
                    
                }
            }
        });
    }

}
