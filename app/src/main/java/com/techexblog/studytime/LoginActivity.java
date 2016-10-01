package com.techexblog.studytime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    //Firebase Instance Variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Initialize views
    @InjectView(R.id.login_email) EditText _emailText;
    @InjectView(R.id.login_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.btn_google_login) com.google.android.gms.common.SignInButton _googleLoginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;
    //@InjectView(R.id.link_continue) TextView _continueLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else{
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //Buttons
        _loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent signUpIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(signUpIntent, REQUEST_SIGNUP);
            }
        });

//        _continueLink.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent continueIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(continueIntent);
//            }
//        });
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private static String randomLoadingMessage() {
        String[] messages = {"Locating the required gigapixels to render...",
                "Spinning up the hamster...",
                "Shovelling coal into the server...",
                "Programming the flux capacitor"};
        Random r = new Random();
        int ran = r.nextInt(3);
        return messages[ran];
    }

    public void login(){
        Log.d(TAG, "Login");

        if(!validate()){
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(randomLoadingMessage());
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //Firebase authentication logic
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        //Once the user is created, it will sign into the app
                        //If it fails, a message will be displayed
                        if(!task.isSuccessful()){
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            onLoginFailed();
                        }
                    }
                });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //On complete call either success or failure
                        onLoginSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    public void onBackPressed(){
        //Don't allow them to go back
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        _loginButton.setEnabled(true);
        startActivity(new Intent(this, HomeActivity.class));
    }

    public void onLoginFailed(){
        Toast.makeText(getBaseContext(), "The hamster is lost. Try Again!", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate(){
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //_emailText.setError(Html.fromHtml("<font color='#44B3C2'>Enter a valid email address</font>"));
            _emailText.setError("email is invalid");
            valid = false;
        }
        else{
            _emailText.setError(null);
        }

        if(password.isEmpty() || password.length() < 8){
            _passwordText.setError("password must be greater than 8 characters");
            valid = false;
        }
        else{
            _passwordText.setError(null);
        }

        return valid;
    }
}