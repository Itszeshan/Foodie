package com.project.zeshan.foodie;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.zeshan.foodie.Common.Common;
import com.project.zeshan.foodie.Model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        printKeyHash();
        FacebookSdk.sdkInitialize(this);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        if(Common.isConnectedToInternet(this))
            checkAuthState();
        else
        {
            Toast.makeText(this, "Please check your connection..", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        mAuth.addAuthStateListener(firebaseAuthListener);

    }

    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.project.zeshan.foodie", PackageManager.GET_SIGNATURES);
            for(Signature signature: info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException  | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void checkAuthState()
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


                                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
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

                else
                {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }


            }
        };
    }

}
