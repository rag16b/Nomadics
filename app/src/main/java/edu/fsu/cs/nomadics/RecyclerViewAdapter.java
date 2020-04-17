package edu.fsu.cs.nomadics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private Context context;
    private ArrayList<String> bookmarkNames = new ArrayList<>();

    public RecyclerViewAdapter(Context context_C, ArrayList<String> bookmarkNames_C) {
            context = context_C;
            bookmarkNames = bookmarkNames_C;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // can't find my layout file for some reason
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bookmarks)
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    // ViewHolder that sets up the recycled contents information
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView bookmarkName;
        RelativeLayout viewHolderLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            bookmarkName = itemView.findViewById(R.id.bookmarkName);
            viewHolderLayout = itemView.findViewById(R.id.viewholder_layout);
        }
    }
}
