package com.rlmax.smartbincollect;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progress_circular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        //  etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        progress_circular = findViewById(R.id.progress_circular);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSignIn) {
            onBackPressed();
        }
        if (view.getId() == R.id.btnSignUp) {
            if (etName.getText().toString().trim().equals("")) {
                etName.setError("Please enter your name");
                etName.requestFocus();
                return;
            }
            if (etEmail.getText().toString().trim().equals("")) {
                etEmail.setError("Please enter Email");
                etEmail.requestFocus();
                return;
            }
            if (etPassword.getText().toString().trim().equals("")) {
                etPassword.setError("Please enter password");
                etPassword.requestFocus();
                return;
            }
            createAccount(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
        }
    }


    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        progress_circular.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString())
                                    .setPhotoUri(Uri.parse("https://cdn.truelancer.com/user-picture/197249-56c33e97159b8.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseMessaging.getInstance().subscribeToTopic("notification");
                                                progress_circular.setVisibility(View.GONE);
                                                openDialog("Sign Up Success", true);
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            //  Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progress_circular.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        // [END create_user_with_email]
    }

    public void openDialog(String title, boolean onlyPositive) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(title);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        SignUpActivity.this.finish();
                    }
                });
        if (!onlyPositive) {
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}