package edu.fsu.cs.nomadics;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<String> names = new ArrayList<>();

    public PlacesRecyclerViewAdapter(Context context_C, ArrayList<String> names_C){
        context = context_C;
        names = names_C;
    }



    @Override
    public PlacesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeslist_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesRecyclerViewAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //where we will be getting the info from

        holder.name.setText(names.get(position));


        holder.placeslistlayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                // Log.d(TAG, "onClick:clicked on " + names.get(position));


                Toast.makeText(context, names.get(position), Toast.LENGTH_SHORT).show();
            }

        });

    }



    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        RelativeLayout placeslistlayout;


        public ViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.locationname);
            placeslistlayout = itemView.findViewById(R.id.placeslistrelativelayout);



        }
    }
}
