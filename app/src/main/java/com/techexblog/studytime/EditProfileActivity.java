package com.techexblog.studytime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    //Firebase Instance Variables

    //EditText Views
    @InjectView(R.id.input_first) EditText _first;
    @InjectView(R.id.input_last) EditText _last;
    //@InjectView(R.id.input_phone) EditText _phone;
    @InjectView(R.id.input_email) EditText _email;
    //@InjectView(R.id.input_password) EditText _password;
    @InjectView(R.id.btn_update_account) Button _update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Log.i(TAG, "injecting");
        ButterKnife.inject(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in: gathering user info");
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();

            //Set EditText hints to current information
            //Split the name into two
            String[] fullName = name.split(" ");
            _first.setHint(fullName[0]);
            _last.setHint(fullName[1]);
            _email.setHint(email);
        }
        else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        _update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccount();
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


    //Currently only updates name
    private void updateAccount() {
        Log.d(TAG, "updateAccount");

        if(!validate()){
            onUpdateFailed();
            return;
        }

        _update.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(randomLoadingMessage());
        progressDialog.show();

        String firstName = _first.getText().toString();
        String lastName = _last.getText().toString();
        //String phone = _phone.getText().toString();
        String email = _email.getText().toString();
        //String password = _password.getText().toString();

        //Firebase Update logic
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .setPhotoUri(Uri.parse(""))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
//        AuthCredential credential = EmailAuthProvider
//                .getCredential("user@example.com", "password1234");
//
//        // Prompt the user to re-provide their sign-in credentials
//        user.reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d(TAG, "User re-authenticated.");
//                    }
//                });
//        user.updateEmail(email)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User email address updated.");
//                        }
//                    }
//                });


        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //On complete call either success or failure
                        onUpdateSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void onUpdateSuccess() {
        _update.setEnabled(true);
        setResult(RESULT_OK, null);
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void onUpdateFailed() {
        Toast.makeText(getBaseContext(), "Oops! Signup failed.", Toast.LENGTH_LONG).show();
        _update.setEnabled(true);
    }

    //validates the email address entered and the password entered - retype if not
    private boolean validate(){
        boolean valid = true;

        String firsName = _first.getText().toString();
        String lastName = _last.getText().toString();
        //String phone = _phone.getText().toString();
        //String email = _email.getText().toString();
        //String password = _password.getText().toString();

        if (firsName.isEmpty()) {
            _first.setError("must enter a first name");
            valid = false;
        } else {
            _first.setError(null);
        }

        if (lastName.isEmpty()) {
            _last.setError("must enter a last name");
            valid = false;
        } else {
            _last.setError(null);
        }
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _email.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _email.setError(null);
//        }
//
//        if (password.isEmpty() || password.length() < 8) {
//            _password.setError("must be atleast alphanumeric characters");
//            valid = false;
//        } else {
//            _password.setError(null);
//        }

        return valid;
    }
}
