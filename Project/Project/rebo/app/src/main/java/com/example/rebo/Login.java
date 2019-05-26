package com.example.rebo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Login extends AppCompatActivity {

    public  Button btnSignUp,btnLogin;
    public EditText edtEmail, edtPassword;
    public  LoginButton btnfb;
    public  ImageView btnImgfb,btnImgGg;
    public CallbackManager callbackManager;
    public  SignInButton googleSignInButton;
    public  GoogleSignInClient googleSignInClient;
    public  String TAG = "Error:" ;
    public FirebaseAuth mAuth;
    private String idToken;
    public SharedPreferences sharedPrefManager;
    public SharedPreferences.Editor editor;
    private final Context mContext = this;

    private Uri photoUri;
    public String uid, email, displayname, avatar, SDT,authEmail, authPassword;
    public FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setControl();
        setEvent();
        loginFaceBook();

    }

    public void setControl(){
        btnSignUp = findViewById(R.id.signup);
        btnLogin = findViewById(R.id.btnlogin);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        btnfb = findViewById(R.id.btnfb);
        btnImgfb = findViewById(R.id.btnImgFb);
        googleSignInButton = findViewById(R.id.btngg);
        btnImgGg = findViewById(R.id.btnImgGg);
        mAuth = FirebaseAuth.getInstance();
        sharedPrefManager = getSharedPreferences("UserInformation", Context.MODE_PRIVATE);
        editor  = sharedPrefManager.edit();

    }
    public void setEvent(){

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               LoginEmail();
            }
        });
        btnImgfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnfb.performClick();
            }
        });
        btnImgGg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginGoogle();
            }
        });

    }

    public void loginGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case 101:
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);

                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e);
                        // ...
                    }

                    break;
            }
        }
    }

    public void loginFaceBook(){
        callbackManager = CallbackManager.Factory.create();
        btnfb.setReadPermissions("email", "public_profile");
        btnfb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Retrieving access token using the LoginResult
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
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
                            final FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();
                            databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null){
                                        createUserFirstAuthentication(user);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            editor.putString("uid",uid);
                            editor.putString("username",user.getDisplayName());
                            editor.putString("email",user.getEmail());
                            editor.putString("phone",user.getPhoneNumber());
                            editor.putString("photo",user.getPhotoUrl().toString());
                            editor.apply();
                            Log.d(TAG, "signInWithCredential:success " + uid);
                            Intent intent = new Intent(Login.this,Online.class);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();

                            databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null){
                                        createUserFirstAuthentication(user);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            editor.putString("uid",uid);
                            editor.putString("username",user.getDisplayName());
                            editor.putString("email",user.getEmail());
                            editor.putString("phone",user.getPhoneNumber());
                            editor.putString("photo",user.getPhotoUrl().toString());
                            editor.apply();
                            Log.d(TAG, "signInWithCredential:success " + uid);
                            Intent intent = new Intent(Login.this,Online.class);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                    }
                });
        }
        private void LoginEmail(){
            authEmail = edtEmail.getText().toString();
            authPassword = edtPassword.getText().toString();
            if (authEmail!=null&&authPassword!=null) {
                mAuth.signInWithEmailAndPassword(authEmail, authPassword)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
//                                     Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    uid = user.getUid();
                                    displayname = user.getDisplayName();
                                    email = user.getEmail();
                                    SDT = user.getPhoneNumber();
                                    databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() == null){
                                                User userCreate = new User(displayname, email, avatar, SDT);
                                                databaseReference.child("users").child(uid).setValue(userCreate);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    editor.putString("uid",uid);
                                    editor.putString("username",user.getDisplayName());
                                    editor.putString("email",user.getEmail());
                                    editor.putString("phone",user.getPhoneNumber());
                                    editor.apply();
                                    Log.d(TAG, "signInWithCredential:success " + uid);
                                    Intent intent = new Intent(Login.this,Online.class);
                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());

                                }

                                // ...
                            }
                        });
            }
            else
                Toast.makeText(this,"SignInWithEmail:failure",Toast.LENGTH_SHORT).show();

        }
        private void createUserFirstAuthentication(FirebaseUser userAuth){
            String guid = userAuth.getUid();
            displayname = userAuth.getDisplayName();
            email = userAuth.getEmail();
            SDT = userAuth.getPhoneNumber();
            avatar = userAuth.getPhotoUrl().toString();
            User userCreate = new User(displayname, email, avatar, SDT);
            databaseReference.child("users").child(guid).setValue(userCreate);

        }

    }