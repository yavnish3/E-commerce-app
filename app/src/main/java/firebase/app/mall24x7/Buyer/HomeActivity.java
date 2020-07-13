package firebase.app.mall24x7.Buyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import firebase.app.mall24x7.Admin.AdminMaintainProductsActivity;
import firebase.app.mall24x7.Model.Products;
import firebase.app.mall24x7.Prevalent.Prevalent;
import firebase.app.mall24x7.R;
import firebase.app.mall24x7.ViewHolder.ProductViewHolder;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference ProductRef;
    private RecyclerView recyclerView;

    private String type="";

    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null)
        {
            type=getIntent().getExtras().get("Admin").toString();
        }


        ProductRef= FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!type.equals("Admin"))
                {
                    Intent intent=new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }

            }
        });



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        TextView usernameView=headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageview=headerView.findViewById(R.id.user_profile_image);

       if (!type.equals("Admin"))
       {
           usernameView.setText(Prevalent.currentUser.getName());
           Picasso.get().load(Prevalent.currentUser.getImage()).placeholder(R.drawable.profile).into(profileImageview);

       }

        recyclerView=findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }


    @Override
    protected void onStart()
    {
        super.onStart();


        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductRef.orderByChild("productState").equalTo("Approved"),Products.class)
                        .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {


                        productViewHolder.txtProductName.setText(products.getPname());
                        productViewHolder.txtProductDescription.setText(products.getDescriotion());
                        productViewHolder.txtProductPrice.setText("Price = Rs "+products.getPrice());

                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);




                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (type.equals("Admin"))
                                {
                                    Intent intent=new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid",products.getPid());
                                    startActivity(intent);

                                }
                                else
                                {
                                    Intent intent=new Intent(HomeActivity.this, ProductDetailActivity.class);
                                    intent.putExtra("pid",products.getPid());
                                    startActivity(intent);
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        ProductViewHolder holder=new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

       // if (id == R.id.action_settings) {
         //   return true;
        //}

        return super.onOptionsItemSelected(item);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {

            if (!type.equals("Admin"))
            {
                Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
                // Handle the camera action
            }

        }
        else if (id == R.id.nav_search)
        {


            if (!type.equals("Admin"))
            {
                Intent intent=new Intent(HomeActivity.this, SearchProductsActivity.class);
                startActivity(intent);

            }

        }
        else if (id == R.id.nav_category)
        {

            if (!type.equals("Admin"))
            {

            }

        }
        else if (id == R.id.nav_setting)
        {

            if (!type.equals("Admin"))
            {

                Intent intent=new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }


        }
        else if (id == R.id.nav_logout)
        {


            if (!type.equals("Admin"))
            {
                Paper.book().destroy();
                Intent intent=new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
