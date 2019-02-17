package com.example.google_maps_android;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import Incident.Incident;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<Incident> incidentArrays = new ArrayList<Incident>();
    PopupWindow popupWindow = new PopupWindow();
    ImageButton incidentButton;
    Dialog dialog;
    ImageView placeMarker;
    Marker mark;


    public void centreOnMapLocation(Location location, String title){
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation));
        //mMap.addCircle(new CircleOptions().center(new LatLng(0,0)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreOnMapLocation(lastKnownLocation,"Your Location");
            }
        }
    }

    private void init(){
        placeMarker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try{
                    if (mark.isInfoWindowShown()){
                        mark.hideInfoWindow();
                    }
                    else{
                        mark.showInfoWindow();
                    }
                }
                catch (NullPointerException nn){

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        fillArray();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dialog = new Dialog(this);
        placeMarker = (ImageView) findViewById(R.id.place_info);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        Intent intent = getIntent();
        if(intent.getIntExtra("Place Number",0)==0){
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //centreOnMapLocation(location,"Your Location");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreOnMapLocation(lastKnownLocation,"Your Location");
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }

        }
        //fillArray();
        //addLocationsToMap();
        mMap.setOnMarkerClickListener(this);

        mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(39.299236, -76.609383)));
        fillArray();
        addLocationsToMap();
        mMap.setOnInfoWindowClickListener(this);
        init();


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }


    public void addLocationsToMap(){
        //mMap.addMarker(new MarkerOptions().position(new LatLng(39.2904, 76.6122)));

        int i ;
        for (i = 0; i < 50; i++){
            String latlong = incidentArrays.get(i).getLocation();

                latlong = latlong.substring(1);
                latlong = latlong.replace("_", ",");
            if(!latlong.equals("ODATA") && !latlong.equals("BCO")) {
                Log.i("Yo", latlong);
                Scanner pickle = new Scanner(latlong);
                pickle.useDelimiter(",");
                String[] toSplit = new String[2];
                //Log.i("Yo",toSplit.toString());
                toSplit[0] = pickle.next();
                toSplit[1] = pickle.next();

                double latit = Double.parseDouble(toSplit[0].replace(" ", ""));
                double longi = Double.parseDouble(toSplit[1].replace(" ", ""));
                LatLng ll = new LatLng(longi, latit);
                String snippet = "Date: " + incidentArrays.get(i).getCalldatetime() + "\n" +
                        "City: " + incidentArrays.get(i).getLocation_address() + "\n" +
                        "Description: " + incidentArrays.get(i).getDescription() + "\n";
                MarkerOptions marker = new MarkerOptions()
                        .position(ll)
                        .title(incidentArrays.get(i).getDescription())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation))
                        .snippet(snippet);
                mark = mMap.addMarker(marker);
            }

            //mMap.addMarker(marker);

        }
        dialog.setContentView(R.layout.custompopup);

        //mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            //@Override
            //public void onInfoWindowClick(Marker marker){
                //try {
                   // dialog.show();
                //}
                //catch(Throwable error){

                //}

            //}
        //});
        //dialog.dismiss();

    }

    public void fillArray(){
        String unorganizedInput = getFile();
        ArrayList<Incident> data = new ArrayList<>();

        try(Scanner input = new Scanner(unorganizedInput))
        {
            while(input.hasNextLine())
            {

                String temp = input.nextLine();


                if(temp.equals(""))
                    break;

                if(!temp.startsWith("\"calldatetime")) {
                    temp = temp.replace("\"", "");
                    temp = temp.replace("POINT", "");
                    temp = temp.replace("(", "");
                    temp = temp.replace(")", "");
                    temp = temp.replace(" ", "_");
                    temp = temp.trim();

                    String[] tempArr = new String[12];

                    Scanner line = new Scanner(temp);
                    line.useDelimiter(",");

                    int counter = 0;
                    try {
                        while (line.hasNext()) {
                            String next = line.next();

                            tempArr[counter] = next;
                            counter++;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }


                    for (int i = 0; i < tempArr.length; i++)  //Fixes blank data
                    {
                        if (tempArr[i].equals("")) {
                            tempArr[i] = "NODATA";
                        }
                    }
                    //String[] tempo = tempArr[5].split(" ");
                    //double latitude = Double.parseDouble(tempo[0]);
                    //double longitude = Double.parseDouble(tempo[1]);
                    //LatLng pickle = new LatLng(latitude,longitude);
                    incidentArrays.add(new Incident(tempArr[0], tempArr[1], tempArr[2], tempArr[3], tempArr[4], tempArr[5], tempArr[6], tempArr[7], tempArr[8], tempArr[9], tempArr[10], tempArr[11]));
                    line.close();
                }
            }
        }

        for(int i = 0; i < 55; i++)
        {
            incidentArrays.get(i).fillLocation();
        }
    }

    public static String getFile() {

        String hostName = "66.175.216.86";
        int portNumber = 3000;

        try (Socket echoSocket = new Socket(hostName, portNumber);
             PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
                     true);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(echoSocket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(
                     new InputStreamReader(System.in))) {

            String output = "";
            String s = in.readLine();
            while(!s.equals("done")) {
                output += s+"\n";
                s = in.readLine();
            }
            return output;

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(
                    "Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
        return "-1";
    }

}
