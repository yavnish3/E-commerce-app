package firebase.app.mall24x7.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import firebase.app.mall24x7.Buyer.login_Activity;
import firebase.app.mall24x7.Buyer.registerActivity;
import firebase.app.mall24x7.R;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginBegin,registerbtn;
    private EditText nameInput,phoneInput,emailInput,passwordInput,addressInput;
    private FirebaseAuth mAuth;
    ProgressDialog lodingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        sellerLoginBegin=findViewById(R.id.seller_login_activity);
        registerbtn=findViewById(R.id.seller_register_btn);
        nameInput=findViewById(R.id.seller_name);
        phoneInput=findViewById(R.id.seller_phone);
        passwordInput=findViewById(R.id.seller_password);
        emailInput=findViewById(R.id.seller_email);
        addressInput=findViewById(R.id.seller_address);

        mAuth=FirebaseAuth.getInstance();

        lodingBar=new ProgressDialog(this);

        sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(SellerRegistrationActivity.this,SellerLoginActivity.class);
                startActivity(intent);
            }
        });

      registerbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              registerSeller();
          }
      });
    }

    private void registerSeller()
    {
        final String name=nameInput.getText().toString();
        final String phone=phoneInput.getText().toString();
        final String password=passwordInput.getText().toString();
        final String email=emailInput.getText().toString();
        final String address=addressInput.getText().toString();

        if (!name.equals("")&&!phone.equals("")&&!password.equals("")&&!email.equals("")&&!address.equals(""))
        {
            lodingBar.setTitle("Creating Seller Account");
            lodingBar.setMessage("please Wait, we are checking....");
            lodingBar.setCanceledOnTouchOutside(false);
            lodingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                final DatabaseReference rootRef;
                                rootRef= FirebaseDatabase.getInstance().getReference();

                                String sid=mAuth.getCurrentUser().getUid();

                                HashMap<String,Object> sellerMap=new HashMap<>();
                                sellerMap.put("sid",sid);
                                sellerMap.put("name",name);
                                sellerMap.put("phone",phone);
                                sellerMap.put("password",password);
                                sellerMap.put("email",email);
                                sellerMap.put("address",address);

                                rootRef.child("Sellers").child(sid).updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful())
                                        {
                                            lodingBar.dismiss();
                                            Intent intent=new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(SellerRegistrationActivity.this, "You are Registered successfully", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Please Complete the Registration form...", Toast.LENGTH_SHORT).show();

        }
    }
}
