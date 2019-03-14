package com.zianbam.yourcommunity.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.zianbam.yourcommunity.InfoActivity;
import com.zianbam.yourcommunity.R;
import com.zianbam.yourcommunity.StartActivity;
import com.zianbam.yourcommunity.UpdateProfileActivity;
import com.zianbam.yourcommunity.UpdateProfilePictureActivity;


public class MenuFragment extends Fragment {

  private TextView logOut,edit_profile, change_profilepic,account_setting;
  FirebaseAuth mAuth;
  GoogleSignInClient mGoogleSignInClient;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_menu, container, false);
    mAuth = FirebaseAuth.getInstance();
    logOut = v.findViewById(R.id.logout);
    edit_profile = v.findViewById(R.id.edit_profile);
    change_profilepic = v.findViewById(R.id.change_imagePic);

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
    mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


    onLogOut();
    clickAcivity();
    v.findViewById(R.id.information).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getContext(), InfoActivity.class));
      }
    });
    return v;
  }

  private void clickAcivity() {

    edit_profile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getContext(),UpdateProfileActivity.class));
      }
    });
    change_profilepic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getContext(),UpdateProfilePictureActivity.class));
      }
    });
  }

  private void onLogOut() {
    logOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
//                            Toast.makeText(getContext(), "LogOut success.", Toast.LENGTH_SHORT).show();
              startActivity(new Intent(getContext(), StartActivity.class));
              ((FragmentActivity)getActivity()).finish();

            } else {
//                            Toast.makeText(getContext(), "LogOut failed", Toast.LENGTH_SHORT).show();
            }

          }
        });
      }
    });
  }



}
