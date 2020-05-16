package com.example.ap2;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ap2.Interface.lOnLoadLocationListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */


public class BlankFragment extends Fragment implements OnMapReadyCallback, GeoQueryEventListener, lOnLoadLocationListener
{

    SupportMapFragment mapFragment;

    public BlankFragment()
    {
        // Required empty public constructor
    }


    ////////////////////////////////////////////
    //////////Variables declaration/////////////
    public GoogleMap mMap;
    private LocationRequest lr;
    private LocationCallback lcb;
    private FusedLocationProviderClient flpc;
    private Marker CurrentUser;
    private DatabaseReference myLocRef;
    public GeoFire geoFire;
    private List<LatLng> dangerAreas;
    private ArrayList<StateItem> stateItems = new ArrayList<>();
    private lOnLoadLocationListener listener;
    doit obj = new doit();
    List<String> stateNames;
    private StateAdaptar adapter;
    //////////////////////////////////////////////


    //Decides what happens when option choosen
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blank, container, false);

        flpc = LocationServices.getFusedLocationProviderClient(this.getActivity());
        //a.doInBackground();
        //a.onPostExecute(amen);
        adapter = new StateAdaptar(stateItems, this.getActivity());
        Dexter.withActivity(this.getActivity()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                obj.execute();
                buildLocationRequest();
                buildLocationCallBack();
                //doInBackground();
                initArea();
                settingGeoFire();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();

        return v;
    }


    //for current user location
    private void buildLocationCallBack()
    {
        lcb = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                if (mMap != null) {

                    geoFire.setLocation("You", new GeoLocation(locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude()),
                            new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    if (CurrentUser != null) CurrentUser.remove();
                                    CurrentUser = mMap.addMarker(new MarkerOptions().position(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()
                                    )).title("You"));
                                    if (mMap != null) {
                                        double rlat = 38;
                                        double rlon = 100;
                                        double llat = 8;
                                        double llong = 66;

                                        LatLngBounds India = new LatLngBounds(new LatLng(llat, llong), new LatLng(rlat, rlon));

                                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(India, 1));
                                    }


                                }
                            });
                }
            }
        };

    }


    //Adds locations to list of areas and also firebase updation
    private void initArea()
    {
        listener = this;

        FirebaseDatabase.getInstance()
                .getReference("DangerousAreas")
                .child("My City")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<MyLatLng> latLngLis = new ArrayList<>();
                        for (DataSnapshot locationSnapShot : dataSnapshot.getChildren()) {
                            MyLatLng latLng = locationSnapShot.getValue(MyLatLng.class);
                            latLngLis.add(latLng);

                        }
                        listener.onLoadLocationSuccess(latLngLis);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onLoadLocationFail(databaseError.getMessage());
                    }
                });

        dangerAreas = new ArrayList<>();
        dangerAreas.add(new LatLng(11.7401, 92.6586));//Andaman 1
        dangerAreas.add(new LatLng(15.9129, 79.7400));//andhra 2
        dangerAreas.add(new LatLng(28.2180, 94.7278));//arunachal 3
        dangerAreas.add(new LatLng(26.2006, 92.9376));//assam 4
        dangerAreas.add(new LatLng(25.0961, 85.3131));//bihar 5
        /*dangerAreas.add(new LatLng(30.7333, 76.7794));//Chandigar 6
        dangerAreas.add(new LatLng(21.2787, 81.8661));//chattisgarh 7
        dangerAreas.add(new LatLng(20.1809, 73.0169));//dadra nagar 8
        dangerAreas.add(new LatLng(28.7041, 77.1025));//delhi 9
        dangerAreas.add(new LatLng(15.2993, 74.1240));//goa 10
        dangerAreas.add(new LatLng(22.2587, 71.1924));//gujarat 11
        dangerAreas.add(new LatLng(29.0588, 76.0856));//haryana 12
        dangerAreas.add(new LatLng(31.1048, 77.1734));//himachal 13
        dangerAreas.add(new LatLng(33.7782, 76.5762));//jammu 14
        dangerAreas.add(new LatLng(23.6102, 85.2799));//jharkhand 15
        dangerAreas.add(new LatLng(15.3173, 75.7139));//karnataka 16
        dangerAreas.add(new LatLng(10.8505, 76.2711));//kerala 17
        dangerAreas.add(new LatLng(34.152588, 77.577049));//ladakh 18
        dangerAreas.add(new LatLng(22.9734, 78.6569));//madhya pradesh 19
        dangerAreas.add(new LatLng(19.7515, 75.7139));//maharashtra 20
        dangerAreas.add(new LatLng(24.6637, 93.9063));//manipur 21
        dangerAreas.add(new LatLng(25.4670, 91.3662));//meghalaya 22
        dangerAreas.add(new LatLng(23.1645, 92.9376));//mizoram 23
        dangerAreas.add(new LatLng(20.9517, 85.0985));//odisha 24
        dangerAreas.add(new LatLng(11.9416, 79.8083));//puducherry 25
        dangerAreas.add(new LatLng(31.1471, 75.3412));//punjab 26
        dangerAreas.add(new LatLng(27.0238, 74.2179));//rajasthan 27
        dangerAreas.add(new LatLng(11.1271, 78.6569));//tamil nadu 28
        dangerAreas.add(new LatLng(18.1124, 79.0193));//telangana 29
        dangerAreas.add(new LatLng(23.9408, 91.9882));//tripura 30
        dangerAreas.add(new LatLng(30.0668, 79.0193));//uttarakhand 31
        dangerAreas.add(new LatLng(26.8467, 80.9462));//uttar pradesh 32
        dangerAreas.add(new LatLng(22.9868, 87.8550));//west bengal 33*/


        FirebaseDatabase.getInstance()
                .getReference("DangerousAreas")
                .child("My City").setValue(dangerAreas)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(BlankFragment.this.getActivity(), "Updated!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BlankFragment.this.getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    //Decides what to do when map is ready and all setup
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        //int i;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        //a.doInBackground();
        if (flpc != null) {
            flpc.requestLocationUpdates(lr, lcb, Looper.myLooper());
        }



    }


    //Setting geofire for user location
    private void settingGeoFire()
    {

        myLocRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(myLocRef);
    }


    //Requests location
    private void buildLocationRequest()
    {

        lr = new LocationRequest();
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lr.setInterval(5000);
        lr.setFastestInterval(3000);
        lr.setSmallestDisplacement(10f);

    }


    //onStop function
    @Override
    public void onStop()
    {
        flpc.removeLocationUpdates(lcb);
        super.onStop();
    }


    //On location entered marked by geofencing
    @Override
    public void onKeyEntered(String key, GeoLocation location)
    {
        sendNotification("AP2", String.format("%s entered the dangerous area", key));
    }

    //On exit from location marked by geofencing
    @Override
    public void onKeyExited(String key)
    {
        sendNotification("AP2", String.format("%s leave the dangerous area", key));
    }

    //On movement from location marked by geofencing
    @Override
    public void onKeyMoved(String key, GeoLocation location)
    {
        sendNotification("AP2", String.format("%s move within the dangerous area", key));
    }


    @Override
    public void onGeoQueryReady()
    {

    }

    @Override
    public void onGeoQueryError(DatabaseError error)
    {
        Toast.makeText(this.getActivity(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
    }


    private void sendNotification(String title, String content)
    {
        Toast.makeText(this.getActivity(), "" + content, Toast.LENGTH_LONG).show();
        String NOTIFICATION_CHANNEL_ID = "AP2_c19";
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Channel Description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getActivity(), NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title).setContentText(content).setAutoCancel(false).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        Notification notification = builder.build();
        notificationManager.notify(new Random().nextInt(), notification);
    }


    @Override
    public void onLoadLocationSuccess(List<MyLatLng> latLngs)
    {
        dangerAreas = new ArrayList<>();
        for (MyLatLng myLatLng : latLngs) {
            LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
            dangerAreas.add(convert);
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(BlankFragment.this);
    }

    @Override
    public void onLoadLocationFail(String message)
    {
        Toast.makeText(this.getActivity(), "" + message, Toast.LENGTH_LONG).show();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////



    //Class for jsoup
    public class doit extends AsyncTask<Void, Void, Void>
    {

        //performs task in background
        @Override
        public Void doInBackground(Void... parameters)
        {
            try
            {
                Document doc = Jsoup.connect("https://www.mohfw.gov.in/").get();
                for (int i=1; i<6; i++ )
                {
                     String numbers = doc.select("table.table.table-striped").select("tr").get(1).select("td").get(2).text();
                     String names = doc.select("table.table.table-striped").select("tr").get(1).select("td").get(1).text();
                    stateItems.add(new StateItem(names, numbers));

                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }


        //Executes doInBackground
        public void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);


                for (LatLng latLng : dangerAreas)
                {
                        /*if ()
                        {for(int i = 1; i<6; i++)
                            {*/
                                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                        .title("Green zone").snippet(stateNames+  " cases:" ));
                                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 50);
                                geoQuery.addGeoQueryEventListener(BlankFragment.this);
                            //}

                       /* } else if ()
                        {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                    .title("Orange zone").snippet( " cases:" ));
                            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 50);
                            geoQuery.addGeoQueryEventListener(BlankFragment.this);
                        } else if ()
                        {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                                    .title("Red zone").snippet(" cases:"));
                            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 50);
                            geoQuery.addGeoQueryEventListener(BlankFragment.this);
                        }*/

                }
            
        }

    }//Async task class
}//BlankFragment class






