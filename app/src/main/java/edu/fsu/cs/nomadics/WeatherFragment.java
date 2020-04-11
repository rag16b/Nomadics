package edu.fsu.cs.nomadics;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Timer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String API_KEY = "c45efeb71f597995b35290a8e59832e6";  // This is the weather API key
    private static final String ARG_PARAM1 = "Arg1";
    private OnWeatherInteractionListener mListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    TextView weatherHeader;
    TextView weatherStatus;
    ProgressBar progressBar;

    int longitude;
    int latitude;
    int weatherID;
    String weatherMain;
    String weatherDesc;
    String weatherIcon;
    String base;
    int temp;
    int feels_like;
    int temp_min;
    int temp_max;
    int pressure;
    int humidity;
    int windSpeed;
    int windDeg;
    int cloudsAll;
    int  dt;
    int type;
    int sysID;
    long message;
    String country;
    int sunrise;
    int sunset;
    int timezone;
    int locationID;
    String locationName;
    int locationCOD;
    //Timer

    class RetrieveFeedTask extends AsyncTask<Void,Void,String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL(API_URL + "id=4164138" + "&appid=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
//            Toast.makeText(getContext(),"got a response",Toast.LENGTH_SHORT).show();


/*
            try {
                // If a value in here comes up as null or 0 chances are the some value failed to init.
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();

                JSONObject coord = object.getJSONObject("coord");
                longitude = coord.getInt("lon");
                latitude = coord.getInt("lat");

                JSONArray weather = object.getJSONArray("weather");
                JSONObject weatherOBJ = weather.getJSONObject(0);
                weatherID = weatherOBJ.getInt("id");
                weatherMain = weatherOBJ.getString("main");
                weatherDesc = weatherOBJ.getString("description");
                weatherIcon = weatherOBJ.getString("icon");

                base = object.getString("base");

                JSONObject mainOBJ = object.getJSONObject("main");
                temp = mainOBJ.getInt("temp");
                feels_like = mainOBJ.getInt("feels_like");
                temp_min = mainOBJ.getInt("temp_min");
                temp_max = mainOBJ.getInt("temp_max");
                pressure = mainOBJ.getInt("pressure");
                humidity = mainOBJ.getInt("humidity");

                JSONObject windOBJ = object.getJSONObject("wind");
                windSpeed = windOBJ.getInt("speed");
                windDeg = windOBJ.getInt("deg");

                JSONObject cloudsOBJ = object.getJSONObject("clouds");
                cloudsAll = cloudsOBJ.getInt("all");

                dt = object.getInt("dt");

                JSONObject sysOBJ = object.getJSONObject("sys");
                type = sysOBJ.getInt("type");
                sysID = sysOBJ.getInt("id");

                // This key doesn't return the value no matter what I try
                //message = sysOBJ.getLong("message");

                country = sysOBJ.getString("country");
                sunrise = sysOBJ.getInt("sunrise");
                sunset = sysOBJ.getInt("sunset");

                timezone = object.getInt("timezone");
                locationID = object.getInt("id");
                locationName = object.getString("name");
                locationCOD = object.getInt("cod");


            } catch (JSONException e) {
                // Appropriate error handling code
                Log.e("Error", e.getMessage());
            }
*/
            weatherHeader.setText(locationName + " Weather ");
            weatherStatus.setText("Weather Status: "+ weatherMain + "\n"+
                    "Temperature: "+ temp + "\n" +
                    "Feels Like: " + feels_like + "\n" +
                    "Max Temperature: " + temp_max + "\n" + "Minimum Temperature" + temp_min + "\n" +
                    "Wind Speed: " + windSpeed + "\n" + "Wind Degree:" + windDeg + "\n" +
                    "Pressure: " + pressure + "\n" + "Humidity: " + humidity);
        }
    }

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View weatherView = inflater.inflate(R.layout.fragment_weather, container, false);
        progressBar = weatherView.findViewById(R.id.progressBar);
        weatherHeader = weatherView.findViewById(R.id.weatherHeader);
        weatherStatus = weatherView.findViewById(R.id.weatherStatus);

        new RetrieveFeedTask().execute();
        Toast.makeText(getContext(),"Tried JSON Objects",Toast.LENGTH_SHORT).show();



        return weatherView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWeatherInteractionListener) {
            mListener = (OnWeatherInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWeatherInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnWeatherInteractionListener {
        void onReturnHome();
    }
}
