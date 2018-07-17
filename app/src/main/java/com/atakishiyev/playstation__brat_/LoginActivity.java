package com.atakishiyev.playstation__brat_;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pass;
    private Button signIn, signUp;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Dialog ThisDialog;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Gözləyin...");
        mProgress.setMessage("Dvijenyalar həyata keçirilir...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        email = findViewById(R.id.emailinput);
        pass = findViewById(R.id.passwordinput);
        signIn = findViewById(R.id.btSignIn);
        signUp = findViewById(R.id.btSignUp);
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                    finish();
                }
            }
        };

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().length()>10 && pass.getText().toString().length()>5) {
                    mProgress.show();
                    auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Məlumatlarda səhv.", Toast.LENGTH_LONG).show();
                                        mProgress.dismiss();
                                    } else {
                                        mProgress.dismiss();
                                        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                                    }
                                }
                            });
                }
                else
                    Toast.makeText(LoginActivity.this, "Məlumatlarda səhv.", Toast.LENGTH_LONG).show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThisDialog = new Dialog(LoginActivity.this);
                ThisDialog.setTitle("İstifadəçi Adı");
                ThisDialog.setContentView(R.layout.username);
                Button az = ThisDialog.findViewById(R.id.submit);
                final EditText uname = ThisDialog.findViewById(R.id.edit);
                az.setEnabled(true);
                az.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(uname.getText().toString().length()>4 && email.getText().toString().length()>10 && pass.getText().toString().length()>5) {
                            mProgress.show();
                            auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Qeydiyyatda səhv! Bir daha yoxlayın.", Toast.LENGTH_LONG).show();
                                                mProgress.dismiss();
                                            }
                                            else {
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(uname.getText().toString()).build();
                                                auth.getCurrentUser().updateProfile(profileUpdates);
                                                mProgress.dismiss();
                                                startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                                                final FirebaseUser user = auth.getCurrentUser();
                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                            Toast.makeText(LoginActivity.this, "Poçtunuza təsdiq e-maili göndərildi.", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                                ThisDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Qeydiyyatda səhv! Bir daha yoxlayın.", Toast.LENGTH_LONG).show();
                    }
                });
                ThisDialog.show();
            }
        });
    }
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }
}
