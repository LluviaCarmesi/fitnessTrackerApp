package com.team3.fitnesstrackerapp3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextMail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null && firebaseUser.isEmailVerified()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            editTextMail = findViewById(R.id.edit_text_email);
            editTextPassword = findViewById(R.id.edit_text_password);
            progressDialog = new ProgressDialog(this);

            firebaseAuth = FirebaseAuth.getInstance();
        }
    }

    public void login(View view) {

        String email = editTextMail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging in");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }

                });

    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
