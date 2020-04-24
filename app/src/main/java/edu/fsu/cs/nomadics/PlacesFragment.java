package edu.fsu.cs.nomadics;



import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlacesFragment extends Fragment implements View.OnClickListener{

    private OnPlacesInteractionListener mListener;
    Button homebutton;
    Button weatherbutton;
    Button bookmarksbutton;
    String TAG = "PlacesFragment";


    Button hotelbutton;
    Button restaurantbutton;
    Button parksbutton;
    Button shopsbutton;

    private ArrayList<String> names = new ArrayList<>();
    private RecyclerView placesrecyclerview;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_places, container, false);

        names.add("Hotel 1");
        names.add("Hotel 2");
        names.add("Hotel 3");

        placesrecyclerview = rootView.findViewById(R.id.placesrecyclerview);
        placesrecyclerview.setLayoutManager((new LinearLayoutManager(getContext())));
        PlacesRecyclerViewAdapter adapter = new PlacesRecyclerViewAdapter(getActivity(), names);
        placesrecyclerview.setAdapter(adapter);


        weatherbutton = (Button) rootView.findViewById(R.id.weatherb);
        homebutton = (Button) rootView.findViewById(R.id.homebutton);
        bookmarksbutton = (Button) rootView.findViewById(R.id.bookmarkb);

        weatherbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
        bookmarksbutton.setOnClickListener(this);


        hotelbutton = (Button) rootView.findViewById(R.id.hotelsbutton);
        restaurantbutton = (Button) rootView.findViewById(R.id.restaurantsbutton);
        parksbutton = (Button) rootView.findViewById(R.id.parksbutton);
        shopsbutton = (Button) rootView.findViewById(R.id.shopsbutton);

        hotelbutton.setOnClickListener(this);
        restaurantbutton.setOnClickListener(this);
        parksbutton.setOnClickListener(this);
        shopsbutton.setOnClickListener(this);


        //initialize places api
        String apiKey = "AIzaSyAh6XsP0jo_LY2dzu1d-YQmBe-EqoXxzas";
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
                                String id = place.getId();
                                String name = place.getName();
                                String address = place.getAddress();
                                String phone = place.getPhoneNumber();
                                OpeningHours hours = place.getOpeningHours();
                                List<PhotoMetadata> photos = place.getPhotoMetadatas();

                                //Launch dialog

//                        Toast.makeText(rootView.getContext(), hours.getWeekdayText().toString(), Toast.LENGTH_LONG).show();

//                        startActivity(new Intent(Intent.ACTION_DIAL,
//                                Uri.fromParts("tel", phone, null)));
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
        /*if(hotelbutton.isPressed())

        if(restaurantbutton.isPressed())

        if(parksbutton.isPressed())

        if(shopsbutton.isPressed())
          */
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


    public interface OnPlacesInteractionListener {
        void onReturnHome();

        void onStartWeather();

        void onStartBookmarks();
    }









}