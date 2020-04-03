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

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // TODO: setup UI
        weatherbutton = (Button) rootView.findViewById(R.id.weatherb);
        placesbutton = (Button) rootView.findViewById(R.id.placesbutton);
        bookmarksbutton = (Button) rootView.findViewById(R.id.bookmarkb);

        weatherbutton.setOnClickListener(this);
        placesbutton.setOnClickListener(this);
        bookmarksbutton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (weatherbutton.isPressed())
            mListener.onStartWeather();
        if (placesbutton.isPressed())
            mListener.onStartPlaces();
        if (bookmarksbutton.isPressed())
            mListener.onStartBookmarks();
    }


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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onStartWeather();

        void onStartPlaces();

        void onStartBookmarks();

    }

}