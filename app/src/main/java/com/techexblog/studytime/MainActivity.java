package com.techexblog.studytime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.login_email) EditText _emailText;
    @InjectView(R.id.login_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;
    @InjectView(R.id.link_continue) TextView _continueLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

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

        _continueLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent continueIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(continueIntent);
            }
        });
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

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(randomLoadingMessage());
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //TODO: IMPLEMENT FIREBASE AUTH LOGIC
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode ==REQUEST_SIGNUP){
            if(resultCode == RESULT_OK){
                //TODO: IMPLEMENT SUCCESSFUL SIGN IN CODE
                //By defauly, activity is finished and we log them in
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed(){
        //Don't allow them to go back
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed(){
        Toast.makeText(getBaseContext(), "The hamster is lost. Try Agin!", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate(){
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //_emailText.setError(Html.fromHtml("<font color='#44B3C2'>Enter a valid email address</font>"));
            _emailText.setError("email is invalid");;
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