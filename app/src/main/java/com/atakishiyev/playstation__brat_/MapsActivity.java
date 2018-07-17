package com.atakishiyev.playstation__brat_;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean marker;
    private FloatingActionButton button;
    private AdView adView;
    private LatLng pos;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Dialog markerDialog;
    private Marker newPlace;
    private ArrayList<Marker> markers;
    private HashMap<String, PSClub> map;

    public String clubID(LatLng latLng){
        return Double.toString(latLng.latitude)+Double.toString(latLng.longitude);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = new HashMap<>();
        marker = false;
        markers = new ArrayList<>();
        markerDialog = new Dialog(this);
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

        button =  findViewById(R.id.floatingButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, AddClubActivity.class);
                intent.putExtra("lat", pos.latitude);
                intent.putExtra("lng", pos.longitude);
                startActivity(intent);
                finish();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Places");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item: dataSnapshot.getChildren()){
                    PSClub club = item.getValue(PSClub.class);
                    markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(club.getLat(), club.getLng())).title(club.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                    String tag = clubID(new LatLng(club.getLat(), club.getLng()));
                    markers.get(markers.size()-1).setTag(tag);
                    map.put(tag, club);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTag()!="new"){
                    final PSClub club = map.get(marker.getTag().toString());
                    markerDialog = new Dialog(MapsActivity.this);
                    markerDialog.setContentView(R.layout.markerinfo);
                    final TextView name = markerDialog.findViewById(R.id.facilityNameText), rate = markerDialog.findViewById(R.id.ratingText),
                            ratecnt = markerDialog.findViewById(R.id.ratecntText), price = markerDialog.findViewById(R.id.priceText),
                            console = markerDialog.findViewById(R.id.consoleText), close = markerDialog.findViewById(R.id.txtclose);
                    Button reviewer = markerDialog.findViewById(R.id.btnReview);
                    reviewer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MapsActivity.this, ReviewActivity.class);
                            intent.putExtra("id", clubID(new LatLng(club.getLat(), club.getLng())));
                            intent.putExtra("size", club.getReviews().size());
                            intent.putExtra("ratesum", club.getRateSum());
                            intent.putExtra("ratecnt", club.getRateCnt());
                            startActivity(intent);
                            finish();
                        }
                    });
                    name.setText(club.getName());
                    ArrayList<String> consoles = club.getConsoles();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Konsollar: ");
                    for (String s : consoles)
                    {
                        sb.append(s);
                        sb.append(", ");
                    }
                    console.setText(sb.toString());

                    rate.setText(String.format("%1$,.2f",club.rating()));
                    ratecnt.setText(club.getRateCnt().toString());
                    price.setText(String.format("%1$,.2f",club.getPrice()));
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            markerDialog.dismiss();
                        }
                    });
                    markerDialog.show();
                }
                return true;
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.406601, 49.867475), 10));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(marker){
                    pos = latLng;
                    mMap.clear();
                    newPlace = mMap.addMarker(new MarkerOptions().position(pos).title("New")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    newPlace.setTag("new");
                    button.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_add_place:
                Snackbar.make(findViewById(R.id.rellay), "Xəritədəki yeri basıb saxla və seç(türkün məsəli)", Snackbar.LENGTH_LONG).show();
                marker = true;
                break;
            case R.id.action_about:
                startActivity(new Intent(MapsActivity.this, AboutActivity.class));
                break;
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
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
