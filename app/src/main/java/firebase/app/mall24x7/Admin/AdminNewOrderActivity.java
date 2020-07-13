package firebase.app.mall24x7.Admin;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import firebase.app.mall24x7.Model.AdminOrder;
import firebase.app.mall24x7.R;
import firebase.app.mall24x7.ViewHolder.AdminOrderView;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderRef,ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);


        orderRef= FirebaseDatabase.getInstance().getReference().child("Orders");

       orderList=findViewById(R.id.order_list);
       orderList.setLayoutManager(new LinearLayoutManager(this));

    }

   @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrder> options=
                new FirebaseRecyclerOptions.Builder<AdminOrder>()
                        .setQuery(orderRef,AdminOrder.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrder, AdminOrderView> adapter=
                new FirebaseRecyclerAdapter<AdminOrder, AdminOrderView>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderView adminOrderViewHolder, final int i, @NonNull final AdminOrder adminOrder) {

                        adminOrderViewHolder.userName.setText("Name:- "+adminOrder.getName());
                        adminOrderViewHolder.userPhone.setText("Phone:- "+adminOrder.getPhone());
                        adminOrderViewHolder.userAddress.setText("Shipping Address:- "+adminOrder.getAddress()+", "+adminOrder.getCity());
                        adminOrderViewHolder.userTPrice.setText("Total Price:- Rs "+adminOrder.getTotalAmount());
                        adminOrderViewHolder.userTime.setText("Order At:- "+adminOrder.getDate()+" "+adminOrder.getTime());

                        adminOrderViewHolder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                String Uid=getRef(i).getKey();

                                Intent intent=new Intent(AdminNewOrderActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid",Uid);
                                startActivity(intent);
                            }
                        });

                        adminOrderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {

                                CharSequence optons[] =new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder=new AlertDialog.Builder(AdminNewOrderActivity.this);
                                builder.setTitle("Have you shipped this order products?");
                                builder.setItems(optons, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {

                                        if (i==0)
                                        {
                                            String Uid=getRef(i).getKey();

                                            RemoveOrder(Uid);

                                        }
                                        else
                                        {
                                            finish();
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrderView(view);
                    }
                };

        orderList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String uid)
    {

        orderRef.child(uid).removeValue();
    }
}
