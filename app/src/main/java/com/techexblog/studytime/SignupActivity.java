package com.techexblog.studytime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    //Firebase Instance Variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @InjectView(R.id.input_first) EditText _firstNameText;
    @InjectView(R.id.input_last) EditText _lastNameText;
    @InjectView(R.id.input_phone) EditText _phoneText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_create_account) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
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

    private void signup() {
        Log.d(TAG, "Signup");

        if(!validate()){
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(randomLoadingMessage());
        progressDialog.show();

        String firsName = _firstNameText.getText().toString();
        String lastName = _lastNameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //Firebase Signup Logic
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                @Override
                public void onComplete(@NonNull Task<AuthResult> task){
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    //Once the user is created, it will sign into the app
                    //If it fails, a message will be displayed
                    if(!task.isSuccessful()){
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        onSignupFailed();
                    }
                }

        });


        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //On complete call either success or failure
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Oops! Signup failed.", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    //validates the email address entered and the password entered - retype if not
    private boolean validate(){
        boolean valid = true;

        String firsName = _firstNameText.getText().toString();
        String lastName = _lastNameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (firsName.isEmpty()) {
            _firstNameText.setError("must enter a first name");
            valid = false;
        } else {
            _firstNameText.setError(null);
        }

        if (lastName.isEmpty()) {
            _lastNameText.setError("must enter a last name");
            valid = false;
        } else {
            _lastNameText.setError(null);
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            _passwordText.setError("must be atleast alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
