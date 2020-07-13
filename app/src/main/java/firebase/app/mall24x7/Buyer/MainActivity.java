package firebase.app.mall24x7.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import firebase.app.mall24x7.Model.User;
import firebase.app.mall24x7.Prevalent.Prevalent;
import firebase.app.mall24x7.R;
import firebase.app.mall24x7.Sellers.SellerHomeActivity;
import firebase.app.mall24x7.Sellers.SellerRegistrationActivity;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinbtn,loginbtn;
    private ProgressDialog lodingBar;
    private TextView sellerLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinbtn=(Button)findViewById(R.id.main_join_btn);
        loginbtn=(Button)findViewById(R.id.main_login_btn);
        sellerLogin=(TextView) findViewById(R.id.seller_login);
        lodingBar=new ProgressDialog(this);

        Paper.init(this);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, login_Activity.class);
                startActivity(intent);
            }
        });
        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, registerActivity.class);
                startActivity(intent);

            }
        });

        sellerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SellerRegistrationActivity.class);
                startActivity(intent);
            }
        });

        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String UserPsswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if(UserPhoneKey!="" && UserPsswordKey!="")
        {
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPsswordKey))
            {

                AllowAccess(UserPhoneKey,UserPsswordKey);
                lodingBar.setTitle("Already Logged In");
                lodingBar.setMessage("please Wait");
                lodingBar.setCanceledOnTouchOutside(true);
                lodingBar.show();
            }
        }
    }



    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser!=null)
        {
            Intent intent=new Intent(MainActivity.this, SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }



    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference rootref;
        rootref= FirebaseDatabase.getInstance().getReference();

        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists())
                {
                    User userData=dataSnapshot.child("Users").child(phone).getValue(User.class);

                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Logged in Successfully.....", Toast.LENGTH_SHORT).show();
                            lodingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentUser=userData;
                            startActivity(intent);
                        }
                        else
                        {
                            lodingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                else
                {
                    Toast.makeText(MainActivity.this, "Account with this"+phone+"number do mot exists", Toast.LENGTH_SHORT).show();
                    lodingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
