package edu.fsu.cs.nomadics;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
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
public class WeatherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String API_5DAY_URL = "http://api.openweathermap.org/data/2.5/forecast?";
    private static final String API_KEY = "c45efeb71f597995b35290a8e59832e6";  // This is the weather API key
    private static final String API_ICON_URL = " http://openweathermap.org/img/wn/";
    private static final String ARG_PARAM1 = "Arg1";
    String unitType = "&units=imperial";
    private OnWeatherInteractionListener mListener;
    SwipeRefreshLayout swipeLayout;
    Fragment frag = null;


    private RecyclerView weatherRecycler;
    private RecyclerView.Adapter weatherAdapter;
    private RecyclerView.LayoutManager weatherManager;

    Switch unitsToggle;



    private String mParam1;
    ProgressBar progressBar;

    TextView weatherHeader;
    TextView feelsLikeTemp;
    TextView tempMinMax;
    TextView tempBase;
    TextView currentWeatherDesc;
    TextView currentdetails_wind;
    TextView currentdetails_humidity;
    TextView currentdetails_pressure;
    TextView currentdetails_sunrise;
    TextView currentdetails_sunset;
    ImageView currentIcon;

    //------------------Current Weather JSON variables
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
    double windSpeed;
    double windDeg;
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
    public void onRefresh() {

        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
            }
        }, 1000);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View weatherView = inflater.inflate(R.layout.fragment_weather, container, false);

        setupCurrentWeather(weatherView);
        new RetrieveFeedTask().execute();
        new Retrieve5DayTask().execute();

        // Recycler Handles the 5 Day Forecast
        weatherRecycler = weatherView.findViewById(R.id.weather_Recycler);
        weatherRecycler.setHasFixedSize(false);

        return weatherView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeLayout = view.findViewById(R.id.swipeLayout_weather);
        swipeLayout.setOnRefreshListener(this);
        frag = this;

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

    public void setupCurrentWeather(View weatherView) {
        progressBar = weatherView.findViewById(R.id.progressBar);
        weatherHeader = weatherView.findViewById(R.id.weatherHeader);
        tempBase = weatherView.findViewById(R.id.textView_temperature);
        tempMinMax = weatherView.findViewById(R.id.textView_tempmaxmin);
        feelsLikeTemp = weatherView.findViewById(R.id.textView_feelslike);
        currentWeatherDesc = weatherView.findViewById(R.id.textView_currentWeatherdesc);
        currentIcon = weatherView.findViewById(R.id.currentWeather_icon);
        currentdetails_wind = weatherView.findViewById(R.id.currentdetails_wind);
        currentdetails_humidity = weatherView.findViewById(R.id.currentdetails_humidity);
        currentdetails_pressure = weatherView.findViewById(R.id.currentdetails_pressure);
        currentdetails_sunrise = weatherView.findViewById(R.id.currentdetails_sunrise);
        currentdetails_sunset = weatherView.findViewById(R.id.currentdetails_sunset);
        unitsToggle = weatherView.findViewById(R.id.switch_Units);
        unitsToggle.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    unitType = "&units=metric";
                } else {
                    unitType = "&units=imperial";
                }
            }
        });

    }



    public void loadCurrentWeather(String response){
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
            windSpeed = windOBJ.getDouble("speed");
            windDeg = windOBJ.getDouble("deg");

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

        weatherHeader.setText(locationName + " Weather ");
        tempBase.setText(temp + "°");
        feelsLikeTemp.setText("Feels like " + feels_like + "°");
        tempMinMax.setText(temp_max + "°"+ "/" + temp_min + "°");
        currentWeatherDesc.setText(weatherDesc);
        if(windDeg == 0)
        {
            currentdetails_wind.setText("Wind Speed: " + windSpeed);
        }
        else
        {
            currentdetails_wind.setText("Wind Speed: " + windSpeed + " " + windDirection(windDeg));
        }
        currentdetails_sunset.setText("Sunset: " + sunset);
        currentdetails_sunrise.setText("Sunrise: " + sunrise);
        currentdetails_pressure.setText("Pressure: " + pressure);
        currentdetails_humidity.setText("Humidity: " + humidity);
    }
    public String windDirection(double windDeg)
    {
        if( (windDeg >= 11.25) && (windDeg <= 33.75) )
        {
            return "NNE";
        }
        else if( (windDeg >= 33.75) && (windDeg <= 56.25) )
        {
            return "NE";
        }
        else if( (windDeg >= 56.25) && (windDeg <= 78.75) )
        {
            return "ENE";
        }
        else if( (windDeg >= 78.75) && (windDeg <= 101.25) )
        {
            return "E";
        }
        else if( (windDeg >= 101.25) && (windDeg <= 123.75) )
        {
            return "ESE";
        }
        else if( (windDeg >= 123.75) && (windDeg <= 146.25) )
        {
            return "SE";
        }
        else if( (windDeg >= 146.25) && (windDeg <= 168.75) )
        {
            return "SSE";
        }
        else if( (windDeg >= 168.75) && (windDeg <= 191.25) )
        {
            return "S";
        }
        else if( (windDeg >= 191.25) && (windDeg <= 213.75) )
        {
            return "SSW";
        }
        else if( (windDeg >= 213.75) && (windDeg <= 236.25) )
        {
            return "SW";
        }
        else if( (windDeg >= 236.25) && (windDeg <= 258.75) )
        {
            return "WSW";
        }
        else if( (windDeg >= 258.75) && (windDeg <= 281.25) )
        {
            return "W";
        }
        else if( (windDeg >= 281.25) && (windDeg <= 303.75) )
        {
            return "WNW";
        }
        else if( (windDeg >= 303.75) && (windDeg <= 326.25) )
        {
            return "NW";
        }
        else if( (windDeg >= 326.25) && (windDeg <= 348.75) )
        {
            return "NNW";
        }
        else if(  ((windDeg >= 348.75) && (windDeg <= 360)) || ((windDeg >= 0) && (windDeg <= 11.25))   )
        {
            return "N";
        }
        return "ERROR";
    }

    public void load5DayWeather(String response) {
        try {
            // If a value in here comes up as null or 0 chances are the some value failed to init.
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();

            // This is the manager for the weather card recycler
            weatherManager = new LinearLayoutManager(getContext());
            weatherRecycler.setLayoutManager(weatherManager);
            weatherAdapter = new WeatherAdapter(object);
            weatherRecycler.addItemDecoration(new DividerItemDecoration(weatherRecycler.getContext(),LinearLayoutManager.VERTICAL));
            weatherRecycler.setAdapter(weatherAdapter);

        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Error", e.getMessage());
        }

    }



    class RetrieveFeedTask extends AsyncTask<Void,Void,String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL(API_URL + "id=4164138"+ unitType + "&appid=" + API_KEY);
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

            loadCurrentWeather(response);
            new RetrieveWeatherIconTask(currentIcon).execute(API_ICON_URL+weatherIcon+"@2x.png");



        }
    }

    class Retrieve5DayTask extends AsyncTask<Void,Void,String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL(API_5DAY_URL + "id=4164138"+ unitType + "&appid=" + API_KEY);
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

            load5DayWeather(response);


        }
    }

    private class RetrieveWeatherIconTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public RetrieveWeatherIconTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String iconurl = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(iconurl).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap response) {
            bmImage.setImageBitmap(response);
        }
    }
}
