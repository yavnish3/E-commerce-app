package firebase.app.mall24x7.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import firebase.app.mall24x7.Model.Products;
import firebase.app.mall24x7.Prevalent.Prevalent;
import firebase.app.mall24x7.R;

public class ProductDetailActivity extends AppCompatActivity {

    private Button addTocartbtn;
    private ImageView productImage;
    private ElegantNumberButton numberBtn;
    private TextView productPrice,productName,productDescription;
    private String productId="",state="Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId=getIntent().getStringExtra("pid");

        addTocartbtn=(Button) findViewById(R.id.add_product_to_cart_btn);
        numberBtn=(ElegantNumberButton)findViewById(R.id.number_btn);
        productImage=(ImageView)findViewById(R.id.product_image_detail);
        productDescription=(TextView) findViewById(R.id.product_description_detail);
        productName=(TextView) findViewById(R.id.product_name_detail);
        productPrice=(TextView) findViewById(R.id.product_price_detail);

        getProductDetails();

        addTocartbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (state.equals("Order Placed")|| state.equals("Order Shipped"))
                {
                    Toast.makeText(ProductDetailActivity.this, "You can purchase more product, once your order is shipped or confirmed.", Toast.LENGTH_LONG).show();

                }
                else
                {
                    addingTOCartList();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    private void addingTOCartList()
    {
        String saveCurrenttime,saveCurrentdate;
        Calendar calForDate=Calendar.getInstance();

        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentdate=currentdate.format(calForDate.getTime());

        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrenttime=currenttime.format(calForDate.getTime());

       final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> cartMap=new HashMap<>();
        cartMap.put("pid",productId);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("quantity",numberBtn.getNumber());
        cartMap.put("date",saveCurrentdate);
        cartMap.put("time",saveCurrenttime);
        cartMap.put("discount","");

        cartListRef.child("Admin View").child(Prevalent.currentUser.getPhone()).child("Products").child(productId)
                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                    cartListRef.child("User View").child(Prevalent.currentUser.getPhone()).child("Products").child(productId)
                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(ProductDetailActivity.this, "Added To Cart List", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(ProductDetailActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    });

                }
            }
        });


    }



    private void getProductDetails()
    {

        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Products products =dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescriotion());
                    productPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void CheckOrderState()
    {
        DatabaseReference ordersRef;
        ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists())
                {
                    String shippingstate=dataSnapshot.child("state").getValue().toString();


                    if (shippingstate.equals("Shipped"))
                    {

                        state="Order Shipped";
                    }
                    else if (shippingstate.equals("Not Shipped"))
                    {

                        state="Order Placed";

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
