package firebase.app.mall24x7.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import firebase.app.mall24x7.Model.Cart;
import firebase.app.mall24x7.Prevalent.Prevalent;
import firebase.app.mall24x7.R;
import firebase.app.mall24x7.ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount,txtMsg1;

    private int overtotalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView=findViewById(R.id.cartlist);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn=(Button)findViewById(R.id.next_Process_btn);
        txtTotalAmount=(TextView)findViewById(R.id.Total_Price);
        txtMsg1=(TextView)findViewById(R.id.msg1);

        final DatabaseReference ref =FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("User View")
                .child(Prevalent.currentUser.getPhone())
                .child("Products");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists())
                        {
                            NextProcessBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent=new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                                    intent.putExtra("Total Price",String.valueOf(overtotalPrice));
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }
                        else
                        {
                            NextProcessBtn.setVisibility(View.GONE);
                            txtTotalAmount.setText("Cart is Empty ");
                            Toast.makeText(CartActivity.this, "Cart is Empty", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options=
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentUser.getPhone())
                                .child("Products"),Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter= new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {

                cartViewHolder.txtProductQuantity.setText("Quantity = "+cart.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price = "+cart.getPrice());
                cartViewHolder.txtproductname.setText(cart.getPname());

                int oneProductTprice=((Integer.valueOf(cart.getPrice())))*Integer.valueOf(cart.getQuantity());
                overtotalPrice=overtotalPrice+oneProductTprice;
                String d=Integer.toString(overtotalPrice);
                txtTotalAmount.setText("Total Price Rs = " + d);

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[]=new CharSequence[]
                                {
                                  "Edit",
                                  "Remove"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i==0)
                                {
                                    Intent intent=new Intent(CartActivity.this, ProductDetailActivity.class);
                                    intent.putExtra("pid",cart.getPid());
                                    startActivity(intent);
                                }
                                if (i==1)
                                {
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentUser.getPhone()).child("Products")
                                            .child(cart.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(CartActivity.this, "Item Removed Successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent=new Intent(CartActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    cartListRef.child("Admin View")
                                            .child(Prevalent.currentUser.getPhone()).child("Products")
                                            .child(cart.getPid()).removeValue();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

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
                    String userName=dataSnapshot.child("name").getValue().toString();

                    if (shippingstate.equals("Shipped"))
                    {
                        txtTotalAmount.setText("Dear, "+userName+"\n Order is shipped successfully");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulation Your Final Order has been shipped Successfully. Soon you recevied your order on your door step.");
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "you can purchase more products, once you recevied your first final order", Toast.LENGTH_SHORT).show();


                    }
                    else if (shippingstate.equals("Not Shipped"))
                    {

                        txtTotalAmount.setText("Shipping State = Not Shipped ");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "you can purchase more products, once you recevied your first final order", Toast.LENGTH_SHORT).show();


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
