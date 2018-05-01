package com.petfinder.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.petfinder.R;
import com.petfinder.dataaccess.entity.Pet;
import com.petfinder.dataaccess.ws.PetWs;
import com.petfinder.dataaccess.ws.Ws;
import com.petfinder.util.Util;
import com.squareup.picasso.Picasso;

import java.io.IOException;

//REMEMBER THIS FRAGMENT HOLDS A MAP VIEW SO YOU ALWAYS NEED TO MAP ITS LIFECYCLE METHODS TO ITS PARENT IN THIS CASE THIS FRAGMENT
public class NewPetFragment extends Fragment implements OnMapReadyCallback {

    private static final long LOCATION_INTERVAL = 5000L;
    private static final int GALLERY_REQUEST_CODE = 3333;

    private PetWs petWs;

    private MapView mapView;
    private GoogleMap googleMap;

    private static final String MAP_VIEW_KEY = "MapViewBundleKey";

    private EditText etName;
    private EditText etAge;
    private EditText etDescription;
    private Spinner spinnerRace;
    private Spinner spinnerSize;
    private FloatingActionButton fabGallery;
    private String imageBase64;
    private String imageExtension;
    private Bundle mapViewBundle;
    private int locationRequestCode;

    private LatLng markerCoors = null;//LatLng(40.71, -74.005)

    private FusedLocationProviderClient fusedLocationClient;

    private View layout;

    private final GoogleMap.OnMapClickListener mapClickListener =  new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            googleMap.clear();
            markerCoors = latLng;
            MarkerOptions opts = new MarkerOptions()
                    .position(markerCoors);
            googleMap.addMarker(opts);
        }
    };

    private final View.OnClickListener fabGalleryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent()
                    .setType("image/*")
                    .setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(i, GALLERY_REQUEST_CODE);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_new_pet, container, false);

        petWs = new PetWs(getContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        this.setHasOptionsMenu(true);


        etName = layout.findViewById(R.id.etName);
        etAge = layout.findViewById(R.id.etAge);
        etDescription = layout.findViewById(R.id.etDesc);
        spinnerRace = layout.findViewById(R.id.spinnerRace);
        spinnerSize = layout.findViewById(R.id.spinnerSize);
        fabGallery = layout.findViewById(R.id.fabGallery);
        mapView = layout.findViewById(R.id.mapViewMap);

        fabGallery.setOnClickListener(fabGalleryClickListener);

        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }

        if(mapView != null) {
            if(Util.PermissionHelper.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getContext())) {
                setupMap();
            } else {
                this.locationRequestCode = Util.PermissionHelper.requestPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data != null && data.getData() != null) {
                Uri dataUri = data.getData();
                String ext = getContext().getContentResolver().getType(dataUri);
                if(ext != null) {
                    imageExtension = ext.substring(ext.indexOf('/') + 1);
                }
                try {
                    byte[] bytes = Util.bitmapToBytes(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), dataUri),
                            imageExtension != null ? imageExtension : "");
                    imageBase64 = Util.toBase64(bytes);
                } catch (IOException ex){
                    throw new RuntimeException(ex);
                }
                System.out.println(imageExtension);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(this.locationRequestCode == requestCode
                && Util.PermissionHelper.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getContext())) {
            setupMap();
        }
        else
            mapView.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuItemSend:
                Pet toInsert = new Pet(
                        0,
                        etName.getText().toString(),
                        Integer.parseInt(etAge.getText().toString()),
                        etDescription.getText().toString(),
                        markerCoors.latitude,
                        markerCoors.longitude,
                        spinnerRace.getSelectedItem().toString(),
                        spinnerSize.getSelectedItem().toString()
                );
                toInsert.setImageBase64(imageBase64);
                toInsert.setImageExtension(imageExtension);

                try {
                    petWs.insert(toInsert, new Ws.WsCallback<Object>() {
                        @Override
                        public void execute(Object response) {
                            if (response != null) {
                                System.out.println(response);
                                Toast.makeText(getContext(), "Mascota insertada", Toast.LENGTH_SHORT).show();
                            } else
                                Util.showAlert("Un error ha ocurrido, asegurese de haber porpocionado una IP y puerto correctos en ajustes", getContext());
                        }
                    });
                    return true;
                } catch (RuntimeException ex){
                    Util.showAlert(ex.getMessage() + "\n Probablemente la IP esta mal formada", getContext());
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null) {
            //IF SAVED INSTANCE WAS NULL IN ON CREATE VIEW THEN CREATE THE BUNDLE MANUALLY OTHERWISE CONTINUE
            Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
            if (mapViewBundle == null) {
                mapViewBundle = new Bundle();
                outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
            }
            mapView.onSaveInstanceState(mapViewBundle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    //SINCE WE ALREADY ASK FOR PERMISSIONS BEFORE ON MAP READY IS EXECUTED
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        if(googleMap != null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            googleMap.setMinZoomPreference(13.0f);

            final Runnable displayPosition = new Runnable() {
                @Override
                public void run() {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerCoors, 18f));
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(markerCoors);
                    googleMap.addMarker(markerOptions);
                    googleMap.setOnMapClickListener(mapClickListener);
                }
            };

            if (markerCoors == null) {
                LocationRequest locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                        .setInterval(LOCATION_INTERVAL)
                        .setFastestInterval(LOCATION_INTERVAL);

                LocationCallback locationCallback = new LocationCallback() {


                    @Override
                    public void onLocationResult(LocationResult locationResult){
                        if(locationResult == null)
                            return;
                        else
                            markerCoors = new LatLng(locationResult.getLocations().get(0).getLatitude(),
                                    locationResult.getLocations().get(0).getLongitude());

                        //REPEAT CODE..., DRY!
                        displayPosition.run();
                        fusedLocationClient.removeLocationUpdates(this);
                    }
                };

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, /*Looper.myLooper()*/ null);

            } else {
                displayPosition.run();
            }
        }
    }

    private void setupMap(){
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }


}
