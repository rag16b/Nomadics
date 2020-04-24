package edu.fsu.cs.nomadics;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Picture;
import android.icu.text.DateIntervalInfo;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlacesFragment extends Fragment implements View.OnClickListener, PlacesRecyclerViewAdapter.OnRecyclerClickListener {
    private OnPlacesInteractionListener mListener;

    String TAG = "PlacesFragment";
    boolean hotel, restaurant, park, attraction;
    final private String apiKey = "AIzaSyAh6XsP0jo_LY2dzu1d-YQmBe-EqoXxzas";
    //arbitrary location in miami
    String latlong = "25.794184,-80.214747";
    PlacesClient client;

    View rootView;
    Button homebutton;
    Button weatherbutton;
    Button bookmarksbutton;
    Button hotelsbutton;
    Button restaurantbutton;
    Button parksbutton;
    Button attractionsbutton;

    private ArrayList<String> names, ids, addrs;
    private ArrayList<Double> lat, lon;
    private ArrayList<JSONObject> photos;
    private RecyclerView placesrecyclerview;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_places, container, false);

        //set up recycler view
        placesrecyclerview = rootView.findViewById(R.id.placesrecyclerview);
        placesrecyclerview.setLayoutManager((new LinearLayoutManager(getContext())));

        //initialize arrays for recycler view
        names = new ArrayList<>();
        ids = new ArrayList<>();
        addrs = new ArrayList<>();
        lat = new ArrayList<>();
        lon = new ArrayList<>();
        photos = new ArrayList<>();

        weatherbutton = (Button) rootView.findViewById(R.id.weatherb);
        homebutton = (Button) rootView.findViewById(R.id.homebutton);
        bookmarksbutton = (Button) rootView.findViewById(R.id.bookmarkb);
        hotelsbutton = (Button) rootView.findViewById(R.id.hotelsbutton);
        restaurantbutton = (Button) rootView.findViewById(R.id.restaurantsbutton);
        parksbutton = (Button) rootView.findViewById(R.id.parksbutton);
        attractionsbutton = (Button) rootView.findViewById(R.id.attractionsbutton);

        weatherbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
        bookmarksbutton.setOnClickListener(this);
        hotelsbutton.setOnClickListener(this);
        restaurantbutton.setOnClickListener(this);
        parksbutton.setOnClickListener(this);
        attractionsbutton.setOnClickListener(this);

        hotel = false;
        restaurant = false;
        park = false;
        attraction = false;

        //initialize places api
        if (!Places.isInitialized())
            Places.initialize(rootView.getContext(), apiKey);

        client = Places.createClient(rootView.getContext());

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

                //gets result of autocomplete
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
                        final LatLng latLng = place.getLatLng();

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
                        textViewAddress.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                        textViewAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //launch maps
                            }
                        });

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

                        ImageButton save = dialogView.findViewById(R.id.placesbookmarkbutton);
                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //save bookmark
                            }
                        });

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
                        Log.d(TAG, "onSuccess: "+place.getId());
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
        else if (homebutton.isPressed())
            mListener.onReturnHome();
        else if (bookmarksbutton.isPressed())
            mListener.onStartBookmarks();
        else if (hotelsbutton.isPressed())
            loadPlaces("lodging");
        else if (restaurantbutton.isPressed())
            loadPlaces("restaurant");
        else if (parksbutton.isPressed())
            loadPlaces("park");
        else if (attractionsbutton.isPressed())
            loadPlaces("tourist_attraction");
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

    //loads the recycler view with
    private void loadPlaces(String type) {
        //checking so we don't have to load the same data each click
        if (type.equals("lodging")) {
            if (hotel)
                return;
            hotel = true;
            restaurant = false;
            park = false;
            attraction = false;
        } else if (type.equals("restaurant")) {
            if (restaurant)
                return;
            hotel = false;
            restaurant = true;
            park = false;
            attraction = false;
        } else if (type.equals("park")) {
            if (park)
                return;
            hotel = false;
            restaurant = false;
            park = true;
            attraction = false;
        }
        else if (type.equals("tourist_attraction") && attraction) {
            hotel = false;
            restaurant = false;
            park = false;
            attraction = true;
        }
        names.clear();
        addrs.clear();
        ids.clear();
        lat.clear();
        lon.clear();

        String radius = "16000";
        Toast.makeText(rootView.getContext(), "Searching", Toast.LENGTH_SHORT).show();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + latlong + "&radius=" + radius +"&type=" + type + "&key=" + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("results");
                            Log.d(TAG, "Setting up recycler view");
                            PlacesRecyclerViewAdapter adapter = null;

                            for (int i = 0; i < array.length(); ++i) {
                                names.add(array.getJSONObject(i).getString("name"));
                                ids.add(array.getJSONObject(i).getString("id"));

                                if (array.getJSONObject(i).has("formatted_address"))
                                    addrs.add(array.getJSONObject(i).getString("formatted_address"));
                                else
                                    addrs.add(null);

                                //get gelocation
                                lat.add(array.getJSONObject(i).getJSONObject("geometry")
                                        .getJSONObject("location").getDouble("lat"));
                                lon.add(array.getJSONObject(i).getJSONObject("geometry")
                                        .getJSONObject("location").getDouble("lng"));
                            }
                            adapter = new PlacesRecyclerViewAdapter(getActivity(), names,
                                    ids, addrs, lat, lon,PlacesFragment.this);
                            placesrecyclerview.setAdapter(adapter);
                        }
                        catch (Exception e){
                            Log.e("Exception:", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error ", error.toString());
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(rootView.getContext());
        queue.add(jsonObjectRequest);
    }

    public interface OnPlacesInteractionListener {
        void onReturnHome();

        void onStartWeather();

        void onStartBookmarks();

        void onStartMaps();
    }

    @Override
    public void onRecyclerClickListener(String name, String addrP, String id, double latitude, double longitude) {
        //Launch dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.places_dialog2, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        //name of place
        TextView textViewName = dialogView.findViewById(R.id.textViewName);
        textViewName.setText(name);

        //address of place
        TextView textViewAddress = dialogView.findViewById(R.id.textViewAddress);
        textViewAddress.setText(addrP);

        ImageButton exit = dialogView.findViewById(R.id.exitbutton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageButton save = dialogView.findViewById(R.id.placesbookmarkbutton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save bookmark
            }
        });

        dialog.show();
    }
}