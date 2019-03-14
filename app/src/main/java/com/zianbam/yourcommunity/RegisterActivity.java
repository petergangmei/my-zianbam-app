package com.zianbam.yourcommunity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button continueBtn;
    private EditText email, password,confirm_password;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        continueBtn = findViewById(R.id.continue_btn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        progressBar = findViewById(R.id.progress_circular);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Create New Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup_with_email_password();
            }
        });
    }

    private void signup_with_email_password() {
        if (!TextUtils.isEmpty(email.getText().toString())&&(!TextUtils.isEmpty(password.getText().toString())
                &&(!TextUtils.isEmpty(confirm_password.getText().toString())))){

            continueBtn.setEnabled(false);

            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("email", email.getText().toString());
                                hashMap.put("id", mAuth.getUid());

                                firebaseDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(getApplicationContext(), AccountSetupActivity.class));
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });


                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                continueBtn.setEnabled(true);
                            }
                        }
                    });

        }else {
            Toast.makeText(this, "All the field are required!", Toast.LENGTH_SHORT).show();
        }
    }
}
