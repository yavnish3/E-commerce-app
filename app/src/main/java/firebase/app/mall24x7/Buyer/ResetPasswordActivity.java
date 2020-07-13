package firebase.app.mall24x7.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import firebase.app.mall24x7.Prevalent.Prevalent;
import firebase.app.mall24x7.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check="";
    private TextView pageTitle,titleQuestions;
    private EditText question1,question2,phoneNumber;
    private Button verify_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        pageTitle=findViewById(R.id.page_title);
        titleQuestions=findViewById(R.id.title_question);
        question1=findViewById(R.id.Question_1);
        question2=findViewById(R.id.Question_2);
        phoneNumber=findViewById(R.id.find_phone_number);
        verify_btn=findViewById(R.id.Question_btn);


        check=getIntent().getStringExtra("check");
    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumber.setVisibility(View.GONE);

        if (check.equals("settings"))
        {
            pageTitle.setText("Set Questions");
            titleQuestions.setText("Please Answer the following Security Questions.");
            verify_btn.setText("set");

            displayPreviousAnswer();

            verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setAnswer();

                }
            });

        }
        else if (check.equals("login"))
        {
            phoneNumber.setVisibility(View.VISIBLE);

            verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    verufyUser();
                }
            });

        }
    }

    private void verufyUser()
    {
        final String phone=phoneNumber.getText().toString();
        final String answer1=question1.getText().toString().toLowerCase();
        final String answer2=question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer1.equals("") && !answer2.equals(""))
        {

            final DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        String mPhone=dataSnapshot.child("phone").getValue().toString();

                        if (dataSnapshot.hasChild("Security Questions"))
                        {
                            String ans1=dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2=dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Your 1st Answer is wrong.", Toast.LENGTH_SHORT).show();

                            }
                            else  if (!ans2.equals(answer2))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Your 2nd Answer is wrong.", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                AlertDialog.Builder builder=new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");

                                final EditText newPassword=new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Write new Password Here");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if (!newPassword.getText().toString().equals(""))
                                        {
                                            ref.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                Toast.makeText(ResetPasswordActivity.this, "Password Change Successfulle", Toast.LENGTH_SHORT).show();

                                                                Intent intent=new Intent(ResetPasswordActivity.this, login_Activity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }

                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        dialogInterface.cancel();

                                    }
                                });
                                builder.show();
                            }


                        }

                        else
                        {
                            Toast.makeText(ResetPasswordActivity.this, "You have not Set the Security Questions.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "This Phone Number does not exists", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        else 
        {
            Toast.makeText(this, "Please complete the form.", Toast.LENGTH_SHORT).show();
        }




    }

    private void setAnswer()
    {
        String answer1=question1.getText().toString().toLowerCase();
        String answer2=question2.getText().toString().toLowerCase();

        if (question1.equals(null) && question2.equals(null))
        {

            Toast.makeText(ResetPasswordActivity.this, "Please Answer the both questions.", Toast.LENGTH_SHORT).show();

        }
        else
        {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentUser.getPhone());

            HashMap<String, Object> userData=new HashMap<>();
            userData.put("answer1",answer1);
            userData.put("answer2",answer2);

            ref.child("Security Questions").updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(ResetPasswordActivity.this, "You have Answer The Security Questions Successfully.", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
    private void displayPreviousAnswer()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentUser.getPhone());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Security Questions").exists())
                {
                    String ans1=dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                    String ans2=dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }
}
