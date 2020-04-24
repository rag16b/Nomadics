package edu.fsu.cs.nomadics;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private Context context;
    private ArrayList<String> bookmarkNames = new ArrayList<>();
    private BookMarkFileIO bmarksIO;

    public RecyclerViewAdapter(Context context_C, ArrayList<String> bookmarkNames_C) {
            context = context_C;
            bookmarkNames = bookmarkNames_C;
            bmarksIO = new BookMarkFileIO(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // can't find my layout file for some reason
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bookmarks, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.bookmarkName.setText(bookmarkNames.get(position));

        holder.viewHolderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + bookmarkNames.get(position));
                //Toast.makeText(context, bookmarkNames.get(position), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("name",bookmarkNames.get(position));
                bundle.putDouble("lat",bmarksIO.getLat(position));
                bundle.putDouble("long",bmarksIO.getLong(position));

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                MapsFragment fragment = new MapsFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bmarksIO.delete(bookmarkNames.get(position));
                bookmarkNames.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookmarkNames.size();
    }


    // ViewHolder that sets up the recycled contents information
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView bookmarkName;
        ImageButton deleteButton;
        RelativeLayout viewHolderLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            bookmarkName = itemView.findViewById(R.id.bookmarkName);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            viewHolderLayout = itemView.findViewById(R.id.viewholder_layout);
        }
    }
}
