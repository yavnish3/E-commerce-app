package firebase.app.mall24x7.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import firebase.app.mall24x7.Admin.AdminHomeActivity;
import firebase.app.mall24x7.Sellers.SellerProductCategoryActivity;
import firebase.app.mall24x7.Model.User;
import firebase.app.mall24x7.Prevalent.Prevalent;
import firebase.app.mall24x7.R;
import io.paperdb.Paper;

public class login_Activity extends AppCompatActivity {

    private EditText inputPhone,inputPassword;
    private Button loginbutten;
    private ProgressDialog lodingBar;
    private String parentDb="Users";
    private CheckBox ChkBoxRememberMe;
    private TextView AdminLink,notAdmin,forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        loginbutten=(Button)findViewById(R.id.login_btn);
        inputPhone=(EditText)findViewById(R.id.login_phno);
        inputPassword=(EditText)findViewById(R.id.login_pswrd);
        AdminLink=(TextView)findViewById(R.id.admin_panel_link);
        notAdmin=(TextView)findViewById(R.id.not_admin_panel_link);
        forgetPassword=(TextView)findViewById(R.id.forget_password_link);

        lodingBar=new ProgressDialog(this);

        ChkBoxRememberMe=(CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent(login_Activity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });


        loginbutten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginbutten.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                notAdmin.setVisibility(View.VISIBLE);
                parentDb="Admins";
        }
        });
        notAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginbutten.setText("Login ");
                AdminLink.setVisibility(View.VISIBLE);
                notAdmin.setVisibility(View.INVISIBLE);
                parentDb="Users";

            }
        });



    }

    private void loginUser() {


        String phone=inputPhone.getText().toString();
        String password=inputPassword.getText().toString();
        if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your Phone Number...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        }

        else{


            lodingBar.setTitle("Login Account");
            lodingBar.setMessage("please Wait, while we are checking....");
            lodingBar.setCanceledOnTouchOutside(false);
            lodingBar.show();

            AllowAccessToAccount(phone,password);

        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {


        if(ChkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }


        final DatabaseReference rootref;
        rootref= FirebaseDatabase.getInstance().getReference();

        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDb).child(phone).exists())
                {
                    User userData=dataSnapshot.child(parentDb).child(phone).getValue(User.class);


                    if (userData != null && userData.getPhone().equals(phone))
                    {
                        if (userData.getPassword().equals(password))
                        {
                            if (parentDb.equals("Admins"))
                            {
                                Toast.makeText(login_Activity.this, "Welcome Admin, you are Logged in Successfully.....", Toast.LENGTH_SHORT).show();
                                lodingBar.dismiss();
                               Intent intent=new Intent(login_Activity.this, AdminHomeActivity.class);

                               startActivity(intent);




                            }
                            else if (parentDb.equals("Users"))
                            {
                                Toast.makeText(login_Activity.this, "Logged in Successfully.....", Toast.LENGTH_SHORT).show();
                                lodingBar.dismiss();
                                Intent intent = new Intent(login_Activity.this, HomeActivity.class);
                                Prevalent.currentUser=userData;
                                startActivity(intent);
                            }

                        }
                        else {
                            lodingBar.dismiss();
                            Toast.makeText(login_Activity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                else
                {
                    Toast.makeText(login_Activity.this, "Account with this"+phone+"number do mot exists", Toast.LENGTH_SHORT).show();
                    lodingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
