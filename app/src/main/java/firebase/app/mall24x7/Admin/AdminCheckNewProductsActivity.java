package firebase.app.mall24x7.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import firebase.app.mall24x7.Interface.ItemClickListner;
import firebase.app.mall24x7.Model.Products;
import firebase.app.mall24x7.R;
import firebase.app.mall24x7.ViewHolder.ProductViewHolder;

public class AdminCheckNewProductsActivity extends AppCompatActivity {

    private RecyclerView approvedList;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_new_products);

        unverifiedProductRef= FirebaseDatabase.getInstance().getReference().child("Products");


        approvedList=findViewById(R.id.approved_product_list);
        approvedList.setHasFixedSize(true);
        approvedList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductRef.orderByChild("productState").equalTo("Not Approved"),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder productViewHolder, int i, @NonNull final Products products)
            {
                productViewHolder.txtProductName.setText(products.getPname());
                productViewHolder.txtProductDescription.setText(products.getDescriotion());
                productViewHolder.txtProductPrice.setText("Price = Rs "+products.getPrice());

                Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

              productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                      final String productId= products.getPid();

                      CharSequence sequence[]=new CharSequence[]
                              {
                                      "Yes",
                                      "No"
                              };
                      AlertDialog.Builder builder=new AlertDialog.Builder(AdminCheckNewProductsActivity.this);
                      builder.setTitle("Do you want to Approved this Product. Are You Sure? ");
                      builder.setItems(sequence, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {

                              if (i==0)
                              {
                                  changeProductState(productId);

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
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder holder=new ProductViewHolder(view);
                return holder;

            }
        };

        approvedList.setAdapter(adapter);
        adapter.startListening();

    }

    private void changeProductState(String productId)
    {

        unverifiedProductRef.child(productId).child("productState")
                .setValue("Approved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(AdminCheckNewProductsActivity.this, "That item has been Approved and it is available for sale from the seller", Toast.LENGTH_SHORT).show();


                    }
                });
    }
}
