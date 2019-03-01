package com.atakishiyev.playstation__brat_;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends AppCompatActivity {

    private RatingBar bar;
    private TextView scale;
    private EditText review;
    private Button button;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String id;
    private Integer size, sum, cnt;
    private ImageButton imageButton;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Gözləyin...");
        mProgress.setMessage("Dvijenyalar həyata keçirilir...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        bar = findViewById(R.id.ratingBar);
        scale = findViewById(R.id.tvRatingScale);
        button = findViewById(R.id.btnSubmitReview);
        imageButton = findViewById(R.id.imageButton);
        review = findViewById(R.id.reviewText);
        auth = FirebaseAuth.getInstance();
        id = getIntent().getStringExtra("id");
        size = getIntent().getIntExtra("size", 0);
        sum = getIntent().getIntExtra("ratesum", 0);
        cnt = getIntent().getIntExtra("ratecnt", 0);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReviewActivity.this, MapsActivity.class));
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(review.getText().toString().length()>0 && size>0){
                    mProgress.show();
                    FirebaseDatabase.getInstance().getReference().child("Places").child(id).child("reviews")
                            .child(auth.getCurrentUser().getDisplayName()).setValue(new PSClub.revData(bar.getRating(), review.getText().toString()));
                    FirebaseDatabase.getInstance().getReference().child("Places").child(id).child("rateSum").setValue(sum + bar.getRating());
                    FirebaseDatabase.getInstance().getReference().child("Places").child(id).child("rateCnt").setValue(cnt+1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mProgress.dismiss();
                            if (task.isSuccessful()) {
                                startActivity(new Intent(ReviewActivity.this, MapsActivity.class));
                                finish();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Xəta.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    Toast.makeText(getApplicationContext(), "Bəs rəy noldu brat?", Toast.LENGTH_SHORT).show();
            }
        });

        bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                scale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        scale.setText("Getməyə dəyməz");
                        break;
                    case 2:
                        scale.setText("Nəm e brat, özün bax da");
                        break;
                    case 3:
                        scale.setText("Ala-babat");
                        break;
                    case 4:
                        scale.setText("Getməyə dəyər");
                        break;
                    case 5:
                        scale.setText("Peçat !");
                        break;
                }
            }
        });
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
