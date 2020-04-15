package edu.fsu.cs.nomadics;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    Button weatherbutton;
    Button placesbutton;
    Button bookmarksbutton;

    //this function is solely the required empty public constructor
    public MainFragment() {
        
    }

    //this oncreateview function is used to set up the UI of this fragment including all the buttons that will be utilized
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        weatherbutton = (Button) rootView.findViewById(R.id.weatherb);
        placesbutton = (Button) rootView.findViewById(R.id.placesbutton);
        bookmarksbutton = (Button) rootView.findViewById(R.id.bookmarkb);

        weatherbutton.setOnClickListener(this);
        placesbutton.setOnClickListener(this);
        bookmarksbutton.setOnClickListener(this);

        return rootView;
    }

    //this function accounts for any button clicks and calls the functions in the main activity to start the fragment pressed
    @Override
    public void onClick(View v) {
        if (weatherbutton.isPressed())
            mListener.onStartWeather();
        if (placesbutton.isPressed())
            mListener.onStartPlaces();
        if (bookmarksbutton.isPressed())
            mListener.onStartBookmarks();
    }


    //this function is used to attach the listener for the interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //this function is used when the app is closed to detach and set the listener to null
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    //this is the interface which is used to communicate to the main activity for changing views
    public interface OnFragmentInteractionListener {
        void onStartWeather();

        void onStartPlaces();

        void onStartBookmarks();

    }

}
