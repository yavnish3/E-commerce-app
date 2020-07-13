package firebase.app.mall24x7.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import firebase.app.mall24x7.Admin.AdminCheckNewProductsActivity;
import firebase.app.mall24x7.Buyer.MainActivity;
import firebase.app.mall24x7.Model.Products;
import firebase.app.mall24x7.R;
import firebase.app.mall24x7.ViewHolder.ItemViewHolder;
import firebase.app.mall24x7.ViewHolder.ProductViewHolder;

public class SellerHomeActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private RecyclerView approvedList;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductRef;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;


                case R.id.navigation_add:
                    Intent intent1=new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
                    startActivity(intent1);
                    return true;


                case R.id.navigation_logout:
                    final FirebaseAuth mAuth=FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Intent intent=new Intent(SellerHomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;


            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        unverifiedProductRef= FirebaseDatabase.getInstance().getReference().child("Products");

        approvedList=findViewById(R.id.seller_home_list);
        approvedList.setHasFixedSize(true);
        approvedList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(unverifiedProductRef.orderByChild("sid")
                                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ItemViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i, @NonNull final Products products)
            {
                itemViewHolder.txtProductName.setText(products.getPname());
                itemViewHolder.txtProductDescription.setText(products.getDescriotion());
                itemViewHolder.txtProductState.setText("States- "+products.getProductState());
                itemViewHolder.txtProductPrice.setText("Price = Rs "+products.getPrice());

                Picasso.get().load(products.getImage()).into(itemViewHolder.imageView);

                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final String productId= products.getPid();

                        CharSequence sequence[]=new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(SellerHomeActivity.this);
                        builder.setTitle("Do you want to Delete this Product. Are You Sure? ");
                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i==0)
                                {
                                    deleteProductState(productId);

                                }
                                if (i==1)
                                {

                                }
                            }
                        });
                        builder.show();

                    }

                });
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view,parent,false);
                ItemViewHolder holder=new ItemViewHolder(view);
                return holder;

            }
        };

        approvedList.setAdapter(adapter);
        adapter.startListening();


    }

    private void deleteProductState(String productId)
    {

        unverifiedProductRef.child(productId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(SellerHomeActivity.this, "That item has been deleted successfully.", Toast.LENGTH_SHORT).show();


                    }
                });

    }
}
