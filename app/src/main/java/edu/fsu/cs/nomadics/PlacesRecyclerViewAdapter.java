package edu.fsu.cs.nomadics;

import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<String> names, address, id;
    private ArrayList<Double> lat, lon;
    private OnRecyclerClickListener onRecyclerClickListener;
    private String name, placeId, addr;

    public interface OnRecyclerClickListener {
        void onRecyclerClickListener(String name, String address, String id,  double latitude, double longitude);
    }

    public PlacesRecyclerViewAdapter(Context context_C, ArrayList<String> names_C,
                                     ArrayList<String> id_C, ArrayList<String> addr,
                                     ArrayList<Double> latitude, ArrayList<Double> longitude,
                                     OnRecyclerClickListener listener) {
        context = context_C;
        names = names_C;
        id = id_C;
        address = addr;
        lat = latitude;
        lon = longitude;
        onRecyclerClickListener = listener;
    }

    @Override
    public PlacesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeslist_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesRecyclerViewAdapter.ViewHolder holder, final int position) {
        //where we will be getting the info from
        holder.name.setText(names.get(position));
        holder.placeslistlayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                //initialize places api
                if (!Places.isInitialized())
                    Places.initialize(context, "AIzaSyAh6XsP0jo_LY2dzu1d-YQmBe-EqoXxzas");

                name = names.get(position);
                placeId = id.get(position);
                addr = address.get(position);
                double latitude = lat.get(position);
                double longitude = lat.get(position);

                onRecyclerClickListener.onRecyclerClickListener(name,
                        addr, placeId, latitude, longitude);
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
