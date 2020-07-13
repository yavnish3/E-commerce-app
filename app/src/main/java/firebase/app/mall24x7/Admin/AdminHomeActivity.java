package firebase.app.mall24x7.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import firebase.app.mall24x7.Buyer.HomeActivity;
import firebase.app.mall24x7.Buyer.MainActivity;
import firebase.app.mall24x7.R;

public class AdminHomeActivity extends AppCompatActivity {

    private Button logoutbtn,checkOrderBtn,maintainBtn,approvedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        logoutbtn=(Button)findViewById(R.id.admin_logout_btn);
        checkOrderBtn=(Button)findViewById(R.id.check_order_btn);
        maintainBtn=(Button)findViewById(R.id.maintain_btn);
        approvedBtn=(Button)findViewById(R.id.check_approved_btn);


        maintainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent intent=new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);

            }
        });



        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent(AdminHomeActivity.this, AdminNewOrderActivity.class);
                startActivity(intent);

            }
        });

        approvedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent(AdminHomeActivity.this, AdminCheckNewProductsActivity.class);
                startActivity(intent);

            }
        });


    }
}
