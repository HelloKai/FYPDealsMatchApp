package com.kaiann.fypdealsmatchapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaiann.fypdealsmatchapp.Model.User;

public class SignUpActivity extends AppCompatActivity {

    private EditText displayname, phonenumber, mEmail, mPassword, cPassword;
    private Button signUp;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        displayname = findViewById(R.id.displayName);
        phonenumber = findViewById(R.id.phoneNumber);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        signUp = findViewById(R.id.signUp);
        cPassword = findViewById(R.id.confirmpassword);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(SignUpActivity.this, DealsHome.class);
                    startActivity(intent);
                    finish();
                    return;
                }

            }
        };
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        final String name = displayname.getText().toString().trim();
                        final String email = mEmail.getText().toString().trim();
                        String password = mPassword.getText().toString().trim();
                        String cpassword = cPassword.getText().toString().trim();
                        final String phone = phonenumber.getText().toString().trim();

                        if(password.equals(cpassword)) {
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                                    (SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this,
                                                        "Sign Up Error: " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {

                                                User user = new User(name, email, phone);

                                                FirebaseDatabase.getInstance().getReference("Users").
                                                        child(mAuth.getCurrentUser().getUid()).setValue(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(SignUpActivity.this,
                                                                            "Registration Successful!",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });


                                            }

                                        }
                                    });
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "Please check your password!", Toast.LENGTH_LONG).show();
                        }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
