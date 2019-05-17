package com.team3.fitnesstrackerapp3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextEmailRegister;
    private EditText editTextPasswordRegister;
    private EditText editTextPasswordConfirm;
    private ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmailRegister = findViewById(R.id.edit_text_email_register);
        editTextPasswordRegister = findViewById(R.id.edit_text_password_register);
        editTextPasswordConfirm = findViewById(R.id.edit_text_password_check);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void register1() {
        String email = editTextEmailRegister.getText().toString();
        String password = editTextPasswordRegister.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, R.string.login_activity_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, R.string.login_activity_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6) {
            Toast.makeText(RegisterActivity.this, R.string.register_activity_characters,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordConfirm.equals(password)) {
        } else {
            Toast.makeText(RegisterActivity.this, R.string.register_activity_passwords, Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { //Creates user using email

                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification() //Sends email verification to email
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, R.string.register_activity_registered, Toast.LENGTH_SHORT).show();
                                                editTextEmailRegister.setText("");
                                                editTextPasswordRegister.setText("");
                                                editTextPasswordConfirm.setText("");
                                            }
                                            else{
                                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, R.string.register_activity_exists, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.register_activity_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }

                });
    }

    public void registerUser(View view) {
        register1();
    }

    public void loginUser(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); //Goes back to login when button is pressed
    }
}
