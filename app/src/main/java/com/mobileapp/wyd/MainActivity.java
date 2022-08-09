package com.mobileapp.wyd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileapp.wyd.adapter.EventListAdapter;
import com.mobileapp.wyd.adapter.FilterListAdapter;
import com.mobileapp.wyd.auth.OnboardActivity;
import com.mobileapp.wyd.data.DataStorage;
import com.mobileapp.wyd.model.Event;
import com.mobileapp.wyd.utils.CustomerMenu;
import com.sanojpunchihewa.updatemanager.UpdateManager;
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    UpdateManager mUpdateManager;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUpdateManager = UpdateManager.Builder(this);
        mUpdateManager.mode(UpdateManagerConstant.IMMEDIATE).start();
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
        {
            startActivity(new Intent(getBaseContext(),OnboardActivity.class));
            finish();
        }
        else
        {
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 100);
        }

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user==null)
                {
                    startActivity(new Intent(getBaseContext(), OnboardActivity.class));
                    finish();
                }
            }
        };

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            enableLocationSettings();
        }
    }

    public void askForPermission(String permission, Integer requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
        else
        {
            enableLocationSettings();
        }
    }


    protected void enableLocationSettings() {
        showLoading();
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) ->
                {
                    setupLocation();
                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(MainActivity.this, 100);
                        } catch (IntentSender.SendIntentException sendEx) {
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    public void setupLocation()
    {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,null).addOnSuccessListener(new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                DataStorage storage=DataStorage.getInstance(getBaseContext());
                storage.setUserLocation(location);
                setupData();
            }
        });
    }

    public void setupData()
    {

        RecyclerView filterRecycler=findViewById(R.id.filter_list);
        TextView tvEmpty=findViewById(R.id.tv_empty);
        filterRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        RecyclerView recyclerView=findViewById(R.id.data_list);
        FilterListAdapter adapter=new FilterListAdapter(this);
        filterRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new FilterListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position)
            {
                adapter.setSelected(position);
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        ArrayList<Event>mList=new ArrayList<Event>();
        EventListAdapter eventListAdapter=new EventListAdapter(getBaseContext(),mList);
        recyclerView.setAdapter(eventListAdapter);
        CollectionReference reference= FirebaseFirestore.getInstance().collection("events");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                mList.clear();
                for(DocumentSnapshot snapshot: value.getDocuments())
                {
                    Event event=snapshot.toObject(Event.class);
                    mList.add(event);
                }
                if(mList.isEmpty())
                {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvEmpty.setVisibility(View.GONE);
                }

                hideLoading();
                eventListAdapter.notifyDataSetChanged();
            }
        });

        CustomerMenu customerMenu=findViewById(R.id.customer_menu);
        customerMenu.setUpdateListener(new CustomerMenu.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(int pos)
            {
                if(pos==2)
                {
                    showLogout();
                }
            }
        });

    }

    public void runTest()
    {
        String [] categories={"Bars","Party","Concert","Social"};
        String[] titles={"Bar Crawl","Party Live","Live Concert","Fashion Show"};
        String[] description={"A bar crawl is fun, festive and one of our favorite social events ideas for exploring a city. Before you begin, plan your route and map it all out for a fully immersive experience. You’ll want to choose a spot that has a good amount of bars to choose from, is easily walkable and has good transportation options to and from your starting point ",
                "Yacht parties are one of the fancier types of social events, but they most certainly make a splash. Begin by selecting the date of your event and setting the budget. Is it a warm afternoon summer yacht party you’re envisioning or a dreamy winter wonderland on the water",
        "Live concers are magical, mysterious and one of the most unique social events ideas. To get started with the planning, pick a theme and encourage attendees to pick their outfits (mask included, of course)",
                "Throwing a fashion show is another one of our most festive social events ideas. Whether you’re hosting it at a venue or a home, find your runway first! This will be the spot where the models stroll up and down, showcasing their meticulously planned outfits. For a full fashion show party, get your guests involved"
        };

        CollectionReference reference=FirebaseFirestore.getInstance().collection("events");

        for(int i=0;i<titles.length;i++)
        {
            String id= reference.document().getId();
            Event event=new Event();
            event.setId(id);
            event.setTitle(titles[i]);
            event.setDescription(description[i]);
            event.setTimestamp(System.currentTimeMillis());
            event.setAddress("J373+7G3, Kashmiri Bazaar Road, Raja Bazar, Rawalpindi, Punjab 46000, Pakistan");
            event.setCategory(categories[i]);
            event.setMales(1);
            event.setFemales(0);
            event.setLat(33.61333586251414);
            event.setLon(73.05391162788248);
            reference.document(id).set(event);
        }



    }

    public void showLogout()
    {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mAuth.signOut();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }


    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        { mAuth.removeAuthStateListener(mAuthListener); }
    }

}