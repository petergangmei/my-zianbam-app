package com.zianbam.yourcommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {
    private Button signIn_Btn;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView signUp_Btn;

//    google signin part
    private static final String TAG = "MainActivity" ;
    private   int RC_SIGN_IN =1 ;
    private SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference databaseReference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progress_circular);
        signUp_Btn = findViewById(R.id.signup_with_email);
        signIn_Btn = findViewById(R.id.signin_with_email_password);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        signInButton = findViewById(R.id.sign_in_button);

        signUp_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTo_signup_page();
            }
        });
        signIn_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin_with_email_password();
            }
        });



//        google signin part
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn_with_gmail();
            }
        });
    }

    private void signIn_with_gmail() {
        pd = new ProgressDialog(StartActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        pd.setIndeterminate(false);
        pd.show();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Login cancelled..", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();


//                            check account status
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()){
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
                                        databaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.exists()){
                                               startActivity(new Intent(StartActivity.this, MainActivity.class));
                                               finish();
                                           }else {
                                               startActivity(new Intent(getApplicationContext(), AccountSetupActivity.class));
                                                finish();
                                           }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }else {
//                                        Toast.makeText(StartActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                                        startActivity(new  Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(StartActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void signin_with_email_password() {
        if (!TextUtils.isEmpty(email.getText().toString())&&!TextUtils.isEmpty(password.getText().toString())){

            pd = new ProgressDialog(StartActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
            pd.setIndeterminate(false);
            pd.show();

            signIn_Btn.setEnabled(false);
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                Toast.makeText(StartActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                finish();
                                pd.dismiss();
                            }else {
                                Toast.makeText(StartActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                pd.dismiss();
                                signIn_Btn.setEnabled(true);

                            }
                        }
                    });
        }else {
            Toast.makeText(this, "Email and Password filed required!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTo_signup_page() {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }
}
