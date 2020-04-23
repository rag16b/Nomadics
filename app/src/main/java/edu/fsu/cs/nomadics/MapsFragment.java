package edu.fsu.cs.nomadics;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private OnMapsInteractionListener mListener;
    private GoogleMap map;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1346;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapsView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        map = googleMap;

        // Checking to make sure location permissions have been granted and forcing user back to home page if they are not
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // getting the devices current location.
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                    }
                    else
                        Toast.makeText(getActivity(), "Unable to find current location", Toast.LENGTH_SHORT).show();
                }
            });

            // will grab coordinates sent from other fragments and set marker here.
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(25.9117,-80.3185))
                    .title("Name of Place"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(25.9117,-80.3185), 10f));

            map.setMyLocationEnabled(true);
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Location Permission Needed")
                    .setMessage("If you would like to use Nomadics' map functionalities, you must allow access to your location.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .create()
                    .show();
            mListener.onReturnHome();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMapsInteractionListener) {
            mListener = (OnMapsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMapsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMapsInteractionListener {
        void onReturnHome();
    }
}
