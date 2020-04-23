package edu.fsu.cs.nomadics;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.icu.text.DateIntervalInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class PlacesFragment extends Fragment implements View.OnClickListener{
    private OnPlacesInteractionListener mListener;

    String TAG = "PlacesFragment";
    boolean hotel, restaurant, park, attraction;
    final private String apiKey = "AIzaSyAh6XsP0jo_LY2dzu1d-YQmBe-EqoXxzas";
    //arbitrary location in miami
    String latlong = "25.794184,-80.214747";

    View rootView;
    Button homebutton;
    Button weatherbutton;
    Button bookmarksbutton;
    Button hotelsbutton;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_places, container, false);

        weatherbutton = (Button) rootView.findViewById(R.id.weatherb);
        homebutton = (Button) rootView.findViewById(R.id.homebutton);
        bookmarksbutton = (Button) rootView.findViewById(R.id.bookmarkb);
        hotelsbutton = (Button) rootView.findViewById(R.id.hotelsbutton);

        weatherbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
        bookmarksbutton.setOnClickListener(this);
        hotelsbutton.setOnClickListener(this);

        hotel = false;
        restaurant = false;
        park = false;
        attraction = false;

        //initialize places api

        if (!Places.isInitialized())
            Places.initialize(rootView.getContext(), apiKey);

        final PlacesClient client = Places.createClient(rootView.getContext());

        //initialize autcomplete search
        AutocompleteSupportFragment auto = (AutocompleteSupportFragment)getChildFragmentManager()
                .findFragmentById(R.id.autocomplete_support_fragment);
        auto.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        //autocompletes to only addresses in the Miami area
        auto.setLocationBias(RectangularBounds.newInstance(
                new LatLng(25.629851, -80.301897),
                new LatLng(25.953478, -80.120961)));

        auto.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //must use client to get info like address, phone number, etc
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                        Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.OPENING_HOURS,
                        Place.Field.PHOTO_METADATAS);
                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(place.getId(),
                        fields);

                client.fetchPlace(request).addOnSuccessListener(
                        new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse response) {
                        //get attributes of place
                        Place place = response.getPlace();
                        String name = place.getName();
                        String address = place.getAddress();
                        final String phone = place.getPhoneNumber();
                        OpeningHours openHours = place.getOpeningHours();
                        List<PhotoMetadata> photo = place.getPhotoMetadatas();

                        //Launch dialog
                        final AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                        View dialogView = getLayoutInflater().inflate(R.layout.places_dialog, null);
                        builder.setView(dialogView);
                        final AlertDialog dialog = builder.create();

                        final ImageView image = dialogView.findViewById(R.id.imageView);
                        if (photo != null && !photo.isEmpty()) {
                            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photo.get(0)).setMaxHeight(150)
                                    .build();
                            client.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                                @Override
                                public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                    image.setImageBitmap(bitmap);
                                }
                            });
                        }

                        //name of place
                        TextView textViewName = dialogView.findViewById(R.id.textViewName);
                        textViewName.setText(name);

                        //address of place
                        TextView textViewAddress = dialogView.findViewById(R.id.textViewAddress);
                        textViewAddress.setText(address);

                        //hours of place
                        TextView textViewHours = dialogView.findViewById(R.id.textViewHoursOpen);
                        String hours = "\n\n\n\n\n\n";
                        if (openHours != null) {
                            hours = openHours.getWeekdayText().toString().replace(", ", "\n");
                            hours = hours.replace("day", "");
                            hours = hours.replace("Wednes", "Wed");
                            hours = hours.replace("Satur", "Sat");
                            hours = hours.replace("[", "").replace("]","");
                        }
                        textViewHours.setText(hours);

                        ImageButton call = dialogView.findViewById(R.id.callbutton);
                        if (phone == null)
                            call.setVisibility(View.GONE);
                        else {
                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Intent.ACTION_DIAL,
                                            Uri.fromParts("tel", phone, null)));
                                }
                            });
                        }

                        ImageButton exit = dialogView.findViewById(R.id.exitbutton);
                        exit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Place not found: " + e.getMessage());
                        Toast.makeText(rootView.getContext(), "An error occurred",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (weatherbutton.isPressed())
            mListener.onStartWeather();
        if (homebutton.isPressed())
            mListener.onReturnHome();
        if (bookmarksbutton.isPressed())
            mListener.onStartBookmarks();
        if (hotelsbutton.isPressed())
            loadPlaces("lodging");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlacesInteractionListener) {
            mListener = (OnPlacesInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadPlaces(String type) {
        if (type.equals("lodging") && hotel)
            return;
        if (type.equals("restaurant") && restaurant)
            return;
        if (type.equals("park") && park)
            return;
        if (type.equals("tourist_attraction") && attraction)
            return;

        String radius = "1000";
        Toast.makeText(rootView.getContext(), "Searching", Toast.LENGTH_SHORT).show();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + latlong + "&radius=" + radius +"&type=" + type + "&key=" + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response = ", response.toString());
                        Toast.makeText(rootView.getContext(), response.toString(), Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("Error ", error.toString());
                        Toast.makeText(rootView.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(rootView.getContext());
        queue.add(jsonObjectRequest);
    }

    public interface OnPlacesInteractionListener {
        void onReturnHome();

        void onStartWeather();

        void onStartBookmarks();
    }
}