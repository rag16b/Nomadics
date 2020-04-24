package edu.fsu.cs.nomadics;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;


public class BookmarkFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "BookmarkFragment";

    private OnBookmarkInteractionListener mListener;
    // UI buttons
    private Button homebutton;
    private Button weatherbutton;
    private Button placesbutton;

    private BookMarkFileIO bmarksIO; // FOR TESTING
    private ArrayList<Pair<String, BookMarkFileIO.Coordinate>> bmNames = new ArrayList<>();
    private ArrayList<String> bookmarks = new ArrayList<>();
    private RecyclerView recyclerView;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bookmark, container, false);
        Log.d(TAG, "onCreateView: ONCREATEVIEW");

        // set onclicks for UI buttons
        homebutton = (Button) rootView.findViewById(R.id.book_homebutton);
        weatherbutton = (Button) rootView.findViewById(R.id.book_weatherbutton);
        placesbutton = (Button) rootView.findViewById(R.id.book_placesbutton);

        homebutton.setOnClickListener(this);
        weatherbutton.setOnClickListener(this);
        placesbutton.setOnClickListener(this);

        // setting up fileIO and syncing arrays
        bmarksIO = new BookMarkFileIO(getContext());
        bmNames = bmarksIO.getBmNames();
        bookmarks = getNames();
        // FOR TESTING
        //bmarksIO.add("Dr. Limon Ceviche Bar", 25.911630, 80.318438);
        //bmarks.delete("Dr. Limon Ceviche Bar");


        recyclerView = rootView.findViewById(R.id.bookmarkRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), bookmarks);
        recyclerView.setAdapter(adapter);

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
        void onStartWeather();
        void onStartPlaces();
    }
}