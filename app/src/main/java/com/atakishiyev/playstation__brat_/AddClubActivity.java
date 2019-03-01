package com.atakishiyev.playstation__brat_;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AddClubActivity extends AppCompatActivity {

    private RatingBar bar;
    private TextView scale;
    private EditText revText, nameText;
    private Button button;
    private HashMap<CheckBox, EditText> checkBoxes;
    private HashMap<String, Double> consoles;
    private HashMap<String, PSClub.revData> reviews;
    private AdView adView;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Intent intent;
    private Double lat, lng;
    private ImageButton imageButton;
    private ProgressDialog mProgress;
    private boolean inputReady, isAnyChecked;

    public void checkCheckboxes(){
        inputReady = true;
        for ( CheckBox key : checkBoxes.keySet() ) {
            if(key.isChecked()){
                if(checkBoxes.get(key).getText().toString().length()==0)
                    inputReady = false;
                isAnyChecked = true;
            }
        }
    }

    public String clubID(LatLng latLng){
        String s = Double.toString(latLng.latitude)+Double.toString(latLng.longitude);
        return s.replace(".","");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);
        inputReady = false;
        isAnyChecked = false;
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Gözləyin...");
        mProgress.setMessage("Dvijenyalar həyata keçirilir...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        MobileAds.initialize(this, "ca-app-pub-6025257147424316~6563918865");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
        intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        bar = findViewById(R.id.ratingBar);
        scale = findViewById(R.id.tvRatingScale);
        button = findViewById(R.id.btnSubmit);
        imageButton = findViewById(R.id.imageButton);
        revText = findViewById(R.id.reviewText);
        nameText = findViewById(R.id.nameText);
        checkBoxes = new HashMap<>();
        checkBoxes.put((CheckBox)findViewById(R.id.ps1), (EditText)findViewById(R.id.ps1Price));
        checkBoxes.put((CheckBox)findViewById(R.id.ps2), (EditText)findViewById(R.id.ps2Price));
        checkBoxes.put((CheckBox)findViewById(R.id.ps3), (EditText)findViewById(R.id.ps3Price));
        checkBoxes.put((CheckBox)findViewById(R.id.ps4), (EditText)findViewById(R.id.ps4Price));
        checkBoxes.put((CheckBox)findViewById(R.id.pc), (EditText)findViewById(R.id.pcPrice));

        for (final CheckBox s : checkBoxes.keySet()) {
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                        ((View)checkBoxes.get(s).getParent()).setVisibility(View.VISIBLE);
                    else{
                        ((View)checkBoxes.get(s).getParent()).setVisibility(View.GONE);
                        checkBoxes.get(s).setText("");
                    }
                }
            });
        }

        reviews = new HashMap<>();
        consoles = new HashMap<>();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddClubActivity.this, MapsActivity.class));
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCheckboxes();
                if(revText.length()>0 && nameText.length()>0 && inputReady && isAnyChecked) {
                    mProgress.show();
                    for (HashMap.Entry<CheckBox, EditText> entry : checkBoxes.entrySet())
                    {
                        String s = entry.getValue().getText().toString();
                        if(!s.contains("."))
                            s+=".0";
                        consoles.put(entry.getKey().getText().toString(), Double.parseDouble(s));
                    }
                    reviews.put(auth.getCurrentUser().getDisplayName(), new PSClub.revData(bar.getRating(), revText.getText().toString()));
                    PSClub club = new PSClub(lat, lng, nameText.getText().toString(), reviews, consoles, (int)bar.getRating(), 1);
                    FirebaseDatabase.getInstance().getReference().child("Places")
                            .child(clubID(new LatLng(lat, lng))).setValue(club).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mProgress.dismiss();
                            if(!task.isSuccessful())
                                Log.d("pecatik", "ERROR!");
                            else{
                                Log.d("pecatik", "SUCCESS!");
                                startActivity(new Intent(AddClubActivity.this, MapsActivity.class));
                                finish();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(getApplicationContext(), "Məlumatlar tam deyil !", Toast.LENGTH_SHORT).show();
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
