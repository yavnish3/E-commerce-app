package firebase.app.mall24x7.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import firebase.app.mall24x7.R;
import firebase.app.mall24x7.Sellers.SellerProductCategoryActivity;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtm,deleteBtn;
    private EditText name,price,description;
    private ImageView imageView;
    private String productId="";
    private DatabaseReference productRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        applyChangesBtm=findViewById(R.id.apply_changes_btn);
        name=findViewById(R.id.product_name_maintain);
        price=findViewById(R.id.product_price_maintain);
        description=findViewById(R.id.product_description_maintain);
        imageView=findViewById(R.id.product_image_maintain);
        deleteBtn=findViewById(R.id.delete_product_btn);


        productId=getIntent().getStringExtra("pid");

        productRef= FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        displayProductinfo();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deletethisProduct();

            }
        });

        applyChangesBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                applyChanges();
            }
        });



    }

    private void deletethisProduct()
    {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Intent intent=new Intent(AdminMaintainProductsActivity.this, SellerProductCategoryActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(AdminMaintainProductsActivity.this, "The Product is deleted Successfully", Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void applyChanges()
    {

        String pName=name.getText().toString();
        String pPrice=price.getText().toString();
        String pDescription=description.getText().toString();

        if (pName.equals(""))
        {
            Toast.makeText(this, "Write Product Name", Toast.LENGTH_SHORT).show();
        }
        else if (pPrice.equals(""))
        {
            Toast.makeText(this, "Entetr The product price", Toast.LENGTH_SHORT).show();
        }
        else if (pDescription.equals(""))
        {
            Toast.makeText(this, "Enter the product Description", Toast.LENGTH_SHORT).show();
        }
        else {

            HashMap<String,Object> productMap=new HashMap<>();
            productMap.put("pid",productId);
            productMap.put("descriotion",pDescription);
            productMap.put("price",pPrice);
            productMap.put("pname",pName);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {

                        Toast.makeText(AdminMaintainProductsActivity.this, "Changes applies Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent(AdminMaintainProductsActivity.this, SellerProductCategoryActivity.class);
                        startActivity(intent);
                        finish();

                    }

                }
            });
        }

    }

    private void displayProductinfo()
    {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    String pName=dataSnapshot.child("pname").getValue().toString();
                    String pPrice=dataSnapshot.child("price").getValue().toString();
                    String pDescription=dataSnapshot.child("descriotion").getValue().toString();
                    String pProductImage=dataSnapshot.child("image").getValue().toString();

                    name.setText(pName);
                    description.setText(pDescription);
                    price.setText(pPrice);
                    Picasso.get().load(pProductImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
