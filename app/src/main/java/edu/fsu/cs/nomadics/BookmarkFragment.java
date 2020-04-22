package edu.fsu.cs.nomadics;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class BookmarkFragment extends Fragment {
    private static final String TAG = "BookmarkFragment";

    private OnBookmarkInteractionListener mListener;
    private ArrayList<String> bookmarks = new ArrayList<>();
    private RecyclerView recyclerView;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bookmark, container, false);

        bookmarks.add("Testing 1");
        bookmarks.add("The Edge Miami");
        bookmarks.add("Testing 1");

        recyclerView = rootView.findViewById(R.id.bookmarkRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), bookmarks);
        recyclerView.setAdapter(adapter);

        return rootView;
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