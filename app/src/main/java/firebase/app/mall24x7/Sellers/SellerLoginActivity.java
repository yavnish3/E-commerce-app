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

import firebase.app.mall24x7.R;

public class SellerLoginActivity extends AppCompatActivity {

    private Button sellerLogin;
    private EditText emailInput,passwordInput;
    ProgressDialog lodingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        passwordInput=findViewById(R.id.seller_login_password);
        emailInput=findViewById(R.id.seller_login_email);

        sellerLogin=findViewById(R.id.seller_login_btn);

        lodingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        sellerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loginSeller();

            }
        });
    }

    private void loginSeller() {
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();


        if (!password.equals("") && !email.equals(""))
        {
            lodingBar.setTitle("Login Seller Account");
            lodingBar.setMessage("please Wait, we are checking....");
            lodingBar.setCanceledOnTouchOutside(false);
            lodingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                lodingBar.dismiss();
                                Intent intent=new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });

        }
        else
        {
            Toast.makeText(this, "Please Complete the Login form...", Toast.LENGTH_SHORT).show();

        }
    }
}
