package edu.fsu.cs.nomadics;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;

public class BookmarkFragment extends Fragment {

    private OnBookmarkInteractionListener mListener;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bookmark, container, false);




        return rootView;
    }
  
    private ArrayList<String> getNames(){
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < bmNames.size(); i++){
            temp.add(bmNames.get(i).first);
        }
        return temp;
    }

    @Override
    public void onClick(View view) {
        if (homebutton.isPressed())
            mListener.onReturnHome();
        if (weatherbutton.isPressed())
            mListener.onStartWeather();
        if (placesbutton.isPressed())
            mListener.onStartPlaces();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBookmarkInteractionListener) {
            mListener = (OnBookmarkInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBookmarkInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBookmarkInteractionListener {
        void onReturnHome();
    }

}