package edu.fsu.cs.nomadics;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


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


    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);

        // TODO: setup UI
        weatherbutton = (Button) rootView.findViewById(R.id.weatherb);
        homebutton = (Button) rootView.findViewById(R.id.homebutton);
        bookmarksbutton = (Button) rootView.findViewById(R.id.bookmarkb);

        weatherbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
        bookmarksbutton.setOnClickListener(this);

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
        if (searchbutton.isPressed())
            mListener.onStartMaps();
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