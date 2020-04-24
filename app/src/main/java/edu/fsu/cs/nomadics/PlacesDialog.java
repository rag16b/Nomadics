package edu.fsu.cs.nomadics;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlacesDialog extends Fragment implements View.OnClickListener{
    private PlacesDialog.OnFragmentInteractionListener mListener;
    ImageButton bookmarkbutton;
    ImageButton phonebutton;
    ImageButton exitbutton;
    TextView address;


    //this function is solely the required empty public constructor
    public PlacesDialog() {

    }

    //this oncreateview function is used to set up the UI of this fragment including all the buttons that will be utilized
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.places_dialog, container, false);

        bookmarkbutton = (ImageButton) rootView.findViewById(R.id.placesbookmarkbutton);
        phonebutton = (ImageButton) rootView.findViewById(R.id.button2);
        exitbutton = (ImageButton) rootView.findViewById(R.id.exitbutton);

        address = (TextView) rootView.findViewById(R.id.textView3);

        address.setOnClickListener(this);
        bookmarkbutton.setOnClickListener(this);
        phonebutton.setOnClickListener(this);
        exitbutton.setOnClickListener(this);

        return rootView;
    }

    //this function accounts for any button clicks and calls the functions in the main activity to start the fragment pressed
    @Override
    public void onClick(View v) {
        if (bookmarkbutton.isPressed())
            mListener.onStartBookmarks();
        if (phonebutton.isPressed())
            mListener.onStartPhone();
        if (exitbutton.isPressed())
            getView().setVisibility(View.GONE);
        if(address.isPressed())
            mListener.onStartMaps();
    }


    //this function is used to attach the listener for the interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlacesDialog.OnFragmentInteractionListener) {
            mListener = (PlacesDialog.OnFragmentInteractionListener) context;
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
        void onStartBookmarks();

        void onStartPhone();

        void onStartMaps();


    }




}







