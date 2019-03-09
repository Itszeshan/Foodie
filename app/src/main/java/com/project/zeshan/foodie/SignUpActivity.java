package com.project.zeshan.foodie;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.zeshan.foodie.Common.Common;
import com.project.zeshan.foodie.Model.User;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText emailEdit,passwordEdit,fNameEdit,lNameEdit,phoneEdit;
    Button btnCancel,btnSignUp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;
    FrameLayout progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();


        
        emailEdit = (EditText) findViewById(R.id.emailEdit_Reg);
        passwordEdit = (EditText) findViewById(R.id.editPassword_Reg);
        fNameEdit = (EditText) findViewById(R.id.fNameEdit_Reg);
        lNameEdit = (EditText) findViewById(R.id.lNameEdit_Reg);
        phoneEdit = (EditText) findViewById(R.id.phoneEdit_Reg);


        btnSignUp = (Button) findViewById(R.id.btnSignUp_Reg);
        btnCancel = (Button) findViewById(R.id.btnCancel_Reg);

        progressbar = (FrameLayout) findViewById(R.id.progressBar_holder_reg);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
                btnSignUp.setEnabled(true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void signUp() {
        btnSignUp.setEnabled(false);
        final String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        final String fname = fNameEdit.getText().toString();
        final String lname = lNameEdit.getText().toString();
        final String phone = phoneEdit.getText().toString();

        if(fname.isEmpty())
        {
            fNameEdit.setError("First Name is required");
            fNameEdit.requestFocus();
            return;
        }

        if(lname.isEmpty())
        {
            lNameEdit.setError("Last name is required");
            lNameEdit.requestFocus();
            return;
        }

        if(email.isEmpty())
        {
            emailEdit.setError("Email is required");
            emailEdit.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEdit.setError("Enter valid email");
            emailEdit.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            passwordEdit.setError("Password is required");
            passwordEdit.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            passwordEdit.setError("Password should be at least 6 characters long");
            passwordEdit.requestFocus();
            return;
        }

        if(phone.isEmpty())
        {
            phoneEdit.setError("phone is required");
            phoneEdit.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        if(Common.isConnectedToInternet(getBaseContext()))
        {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(!dataSnapshot.exists())
                                {
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("email", email);
                                    userMap.put("name", fname + " " + lname);
                                    userMap.put("fname",fname);
                                    userMap.put("phone", phone);
                                    userRef.updateChildren(userMap);
                                }
                                String name = fname + " " + lname;
                                Common.currentUser = new User(email,name,fname, phone);

                                progressbar.setVisibility(View.INVISIBLE);

                                Intent intent = new Intent(SignUpActivity.this,HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                    else
                    {
                        Toast.makeText(SignUpActivity.this, "Sign up error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            progressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Please check your connection..", Toast.LENGTH_SHORT).show();
        }


        
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
