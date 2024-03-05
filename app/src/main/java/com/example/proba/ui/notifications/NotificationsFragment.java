package com.example.proba.ui.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.proba.Incidencia;
import com.example.proba.R;
import com.example.proba.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;



    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference base = FirebaseDatabase.getInstance().getReference();

    DatabaseReference users = base.child("users");
    DatabaseReference uid = users.child(auth.getUid());
    DatabaseReference incidencies = uid.child("incidencies");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.map.setTileSource(TileSourceFactory.MAPNIK);
        binding.map.setMultiTouchControls(true);
        IMapController mapController = binding.map.getController();
        mapController.setZoom(14.5);
        //GeoPoint startPoint = new GeoPoint(39.8209, -0.2243);
        //mapController.setCenter(startPoint);

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), binding.map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();

        binding.map.getOverlays().add(mLocationOverlay);

        CompassOverlay compassOverlay = new CompassOverlay(requireContext(), new InternalCompassOrientationProvider(requireContext()), binding.map);
        compassOverlay.enableCompass();
        binding.map.getOverlays().add(compassOverlay);
        requestPermissionsIfNecessary(new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }
        );
        
        incidencies.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Incidencia incidencia = snapshot.getValue(Incidencia.class);
                Marker m = new Marker(binding.map);
                m.setPosition(new GeoPoint(Double.parseDouble(incidencia.getLatitud()), Double.parseDouble(incidencia.getLongitud())));
                m.setTextLabelFontSize(40);
                m.setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                m.setTitle(incidencia.getDireccio());
                m.setSnippet(incidencia.getProblema());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener(){
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        /*String imagen = "https://firebasestorage.googleapis.com/v0/b/incivisme-bcdca.appspot.com/o/images%2F";

https://firebasestorage.googleapis.com/v0/b/incivisme-rafa.appspot.com/o/images%2F1cf649b4-d36f-46a5-a021-2c4aa0a19fa0?alt=media&token=ba47e2f9-b9df-4407-b7d2-1c5bdb848875
*/


                        /*Han cambiado los tokens*/

                        String imagen = "https://firebasestorage.googleapis.com/v0/b/incivisme-rafa.appspot.com/o/images%2F";

                      //  Glide.with(getContext()).load(imagen + incidencia.getUrl() + "?alt=media&token=40e19299-343b-4b0d-ad3d-a27170ad5cc4"
                        Glide.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/incivisme-rafa.appspot.com/o/images%2F8b2b56ac-e779-4bc5-acfc-d06bccfe6cd9?alt=media&token=40e19299-343b-4b0d-ad3d-a27170ad5cc4").into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                                ImageView imageView = new ImageView(getContext());
                                imageView.setImageDrawable(resource);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setView(imageView);
                                builder.setTitle(incidencia.getDireccio());
                                builder.create().show();
                            }
                        });
                        return true;
                    }
                });
                binding.map.getOverlays().add(m);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      //  IncivismeAPI incivismeAPI = retrofit.create(IncivismeAPI.class);

     /* buttonFoto.setOnClickListener(button -> {
            dispatchTakePictureIntent();
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        binding.map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        binding.map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public Bitmap getBitmapFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }






}