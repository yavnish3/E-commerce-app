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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import firebase.app.mall24x7.R;

public class registerActivity extends AppCompatActivity {

    private Button createaccountbtn;
    private EditText inputName,inputPhoneNo,inputPassword;
    ProgressDialog lodingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        createaccountbtn=(Button)findViewById(R.id.reg_btn);
        inputName=(EditText)findViewById(R.id.reg_urnm);
        inputPhoneNo=(EditText)findViewById(R.id.reg_phno);
        inputPassword=(EditText)findViewById(R.id.reg_pswrd);
        lodingBar=new ProgressDialog(this);


        createaccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatAccount();
            }
        });


    }

    private void creatAccount() {
        String name=inputName.getText().toString();
        String phone=inputPhoneNo.getText().toString();
        String password=inputPassword.getText().toString();
        
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
        else{

            lodingBar.setTitle("Create Account");
            lodingBar.setMessage("please Wait, we are checking....");
            lodingBar.setCanceledOnTouchOutside(false);
            lodingBar.show();

            validatePhone(name,phone,password);

        }
    }

    private void validatePhone(final String name, final String phone, final String password) {
        final DatabaseReference rootref;
        rootref= FirebaseDatabase.getInstance().getReference();

        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userData=new HashMap<>();
                    userData.put("phone",phone);
                    userData.put("password",password);
                    userData.put("name",name);

                    rootref.child("Users").child(phone).updateChildren(userData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(registerActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                        lodingBar.dismiss();
                                        Intent intent=new Intent(registerActivity.this, login_Activity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        lodingBar.dismiss();
                                        Toast.makeText(registerActivity.this, "Network Error: Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
                else {

                    lodingBar.dismiss();

                    Toast.makeText(registerActivity.this, "this  "+phone+"  already exists, Please use another phone number", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
