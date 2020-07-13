package firebase.app.mall24x7.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import firebase.app.mall24x7.Prevalent.Prevalent;
import firebase.app.mall24x7.R;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEDTXT,phoneEDTXT,addressEDTXT,cityEDTXT;
    private Button confirmorderbtn;
    String totalAmount="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Amount Rs = "+totalAmount, Toast.LENGTH_SHORT).show();

        confirmorderbtn=(Button)findViewById(R.id.confirm_final_order_btn);
        nameEDTXT=(EditText) findViewById(R.id.shipment_name);
        phoneEDTXT=(EditText) findViewById(R.id.shipment_phone);
        cityEDTXT=(EditText) findViewById(R.id.shipment_city);
        addressEDTXT=(EditText) findViewById(R.id.shipment_address);

        confirmorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });
    }

    private void Check()
    {

        if (TextUtils.isEmpty(nameEDTXT.getText().toString()))
        {
            Toast.makeText(this, "Enter Your Full Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEDTXT.getText().toString()))
        {
            Toast.makeText(this, "Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEDTXT.getText().toString()))
        {
            Toast.makeText(this, "Enter Your Full Address", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityEDTXT.getText().toString()))
        {
            Toast.makeText(this, "Enter Your City", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {

        final String saveCurrenttime,saveCurrentdate;
        Calendar calForDate=Calendar.getInstance();

        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentdate=currentdate.format(calForDate.getTime());

        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrenttime=currenttime.format(calForDate.getTime());

        final DatabaseReference orderref=FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentUser.getPhone());
        HashMap<String,Object> ordermap=new HashMap<>();
        ordermap.put("totalAmount",totalAmount);
        ordermap.put("name",nameEDTXT.getText().toString());
        ordermap.put("phone",phoneEDTXT.getText().toString());
        ordermap.put("address",addressEDTXT.getText().toString());
        ordermap.put("city",cityEDTXT.getText().toString());
        ordermap.put("date",saveCurrentdate);
        ordermap.put("time",saveCurrenttime);
        ordermap.put("state","Not Shipped");

        orderref.updateChildren(ordermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View").child(Prevalent.currentUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ConfirmFinalOrderActivity.this, "Your final Order has been placed Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                }

            }
        });
    }
}
