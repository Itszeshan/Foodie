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

public class SignInActivity extends AppCompatActivity {


    EditText emailEdit,passwordEdit;
    Button btnCancel,btnSignIn;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;
    FrameLayout progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();


        if(Common.isConnectedToInternet(getBaseContext()))
        {

            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user != null)
                    {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    Map<String, Object> map = (HashMap<String, Object>)dataSnapshot.getValue();

                                    String name = map.get("name").toString();
                                    String fname = map.get("fname").toString();
                                    String email = map.get("email").toString();
                                    String phone = map.get("phone").toString();

                                    User user = new User(email,name,fname,phone);

                                    Common.currentUser = user;

                                    progressbar.setVisibility(View.INVISIBLE);

                                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }
            };

            mAuth.addAuthStateListener(firebaseAuthListener);
        }
        else
        {
            Toast.makeText(this, "Please check your connection..", Toast.LENGTH_SHORT).show();
        }



        emailEdit = (EditText) findViewById(R.id.emailEdit_Login);
        passwordEdit = (EditText) findViewById(R.id.editPassword_Login);

        btnSignIn = (Button) findViewById(R.id.btnSignIn_Login);
        btnCancel = (Button) findViewById(R.id.btnCancel_Login);

        progressbar = (FrameLayout) findViewById(R.id.progressBar_holder_sign_in);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void signIn() {

        btnSignIn.setEnabled(false);
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

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

        progressbar.setVisibility(View.VISIBLE);

        if(Common.isConnectedToInternet(getBaseContext()))
        {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful())
                    {
                        Toast.makeText(SignInActivity.this, "Sign In Error!",Toast.LENGTH_SHORT).show();
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
