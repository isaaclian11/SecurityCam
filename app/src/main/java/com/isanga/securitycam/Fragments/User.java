package com.isanga.securitycam.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.isanga.securitycam.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class User extends Fragment {

    private GoogleSignInClient signInClient;
    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "User";

    private TextView username;
    private Button signinBtn;
    private Button signoutBtn;

    public User() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);
        mAuth = FirebaseAuth.getInstance();

        username = view.findViewById(R.id.user_id);
        signinBtn = view.findViewById(R.id.user_signinBtn);
        signoutBtn = view.findViewById(R.id.user_signoutBtn);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }
        });

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signout();
            }
        });

        checkLoginState();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                startAuth(account);
            } catch (ApiException e) {
                // TODO
            }
        }
    }

    private void startAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateName(user);
                        } else {
                            //TODO
                        }
                    }
                });
    }

    private void updateName(FirebaseUser user){
        if(user!=null) {
            String name = user.getDisplayName();
            username.setText(name);
        }
        else{
            username.setText("");
        }
    }

    private void checkLoginState(){
        FirebaseUser user = mAuth.getCurrentUser();
        updateName(user);
    }

    private void signin(){
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void signout(){
        FirebaseAuth.getInstance().signOut();
        updateName(null);
    }

}
