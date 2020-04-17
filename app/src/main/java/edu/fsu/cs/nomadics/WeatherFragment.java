package edu.fsu.cs.nomadics;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
public class WeatherFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String API_5DAY_URL = "http://api.openweathermap.org/data/2.5/forecast?";
    private static final String API_KEY = "c45efeb71f597995b35290a8e59832e6";  // This is the weather API key
    private static final String API_ICON_URL = " http://openweathermap.org/img/wn/";
    private static final String ARG_PARAM1 = "Arg1";
    String unitType = "&units=imperial";
    private OnWeatherInteractionListener mListener;

    private String mParam1;
    ProgressBar progressBar;

    TextView weatherHeader;
    TextView weatherStatus;
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


    //-------5 Day Layout Elements
    TextView fiveDayHeader;
    TextView[] cardDate = new TextView[40];
    ImageView[] cardIcon= new ImageView[40];
    TextView[] cardTemperature = new TextView[40];
    TextView[] cardSummary = new TextView[40];





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
    //----------------------------------------------------------
    //--------------------------5 Day Weather JSON variables
    int week_cnt;
    String week_cod;
    String week_locationName;
    String week_country;
    double week_latitude;
    double week_longitude;
    int week_sunrise;
    int week_sunset;
    int week_timezone;
    int[] week_dt;
    int[] week_temp;
    int[] week_feelslike;
    int[] week_temp_min;
    int[] week_temp_max;
    int[] week_pressure;
    int[] week_sea_level;
    int[] week_grnd_level;
    int[] week_humidity;
    int[] week_temp_kf;
    int[] week_weather_id;
    String[] week_weather_main;
    String[] week_weather_desc;
    String[] week_weather_icon;
    int[] week_clouds_all;
    double[] week_wind_speed;
    double[] week_wind_deg;
    int[] week_rain3h; // Rain Volume for last 3 hours, units: mm
    int[] week_snow3h; // Snow Volume for last 3 hours
    String[] week_sys_pod;
    String[] week_date;


    TextView fiveDayHeads;



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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View weatherView = inflater.inflate(R.layout.fragment_weather, container, false);
        setupCurrentWeather(weatherView);
        setup5DayWeather(weatherView);


        new RetrieveFeedTask().execute();

        new Retrieve5DayTask().execute();

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

    public void setupCurrentWeather(View weatherView) {
        progressBar = weatherView.findViewById(R.id.progressBar);
        weatherHeader = weatherView.findViewById(R.id.weatherHeader);
        weatherStatus = weatherView.findViewById(R.id.weatherStatus);
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
    }
    public void setup5DayWeather(View weatherView){
        setupCardDate(weatherView);
        setupCardTemperature(weatherView);
        setupCardSummary(weatherView);
        setupCardIcon(weatherView);
    }

    public void setupCardIcon(View weatherView) {
        cardIcon[0] = weatherView.findViewById(R.id.imageView_cardIcon0);
        cardIcon[1] = weatherView.findViewById(R.id.imageView_cardIcon1);
        cardIcon[2] = weatherView.findViewById(R.id.imageView_cardIcon2);
        cardIcon[3] = weatherView.findViewById(R.id.imageView_cardIcon3);
        cardIcon[4] = weatherView.findViewById(R.id.imageView_cardIcon4);
        cardIcon[5] = weatherView.findViewById(R.id.imageView_cardIcon5);
        cardIcon[6] = weatherView.findViewById(R.id.imageView_cardIcon6);
        cardIcon[7] = weatherView.findViewById(R.id.imageView_cardIcon7);
        cardIcon[8] = weatherView.findViewById(R.id.imageView_cardIcon8);
        cardIcon[9] = weatherView.findViewById(R.id.imageView_cardIcon9);
        cardIcon[10] = weatherView.findViewById(R.id.imageView_cardIcon10);
        cardIcon[11] = weatherView.findViewById(R.id.imageView_cardIcon11);
        cardIcon[12] = weatherView.findViewById(R.id.imageView_cardIcon12);
        cardIcon[13] = weatherView.findViewById(R.id.imageView_cardIcon13);
        cardIcon[14] = weatherView.findViewById(R.id.imageView_cardIcon14);
        cardIcon[15] = weatherView.findViewById(R.id.imageView_cardIcon15);
        cardIcon[16] = weatherView.findViewById(R.id.imageView_cardIcon16);
        cardIcon[17] = weatherView.findViewById(R.id.imageView_cardIcon17);
        cardIcon[18] = weatherView.findViewById(R.id.imageView_cardIcon18);
        cardIcon[19] = weatherView.findViewById(R.id.imageView_cardIcon19);
        cardIcon[20] = weatherView.findViewById(R.id.imageView_cardIcon20);
        cardIcon[21] = weatherView.findViewById(R.id.imageView_cardIcon21);
        cardIcon[22] = weatherView.findViewById(R.id.imageView_cardIcon22);
        cardIcon[23] = weatherView.findViewById(R.id.imageView_cardIcon23);
        cardIcon[24] = weatherView.findViewById(R.id.imageView_cardIcon24);
        cardIcon[25] = weatherView.findViewById(R.id.imageView_cardIcon25);
        cardIcon[26] = weatherView.findViewById(R.id.imageView_cardIcon26);
        cardIcon[27] = weatherView.findViewById(R.id.imageView_cardIcon27);
        cardIcon[28] = weatherView.findViewById(R.id.imageView_cardIcon28);
        cardIcon[29] = weatherView.findViewById(R.id.imageView_cardIcon29);
        cardIcon[30] = weatherView.findViewById(R.id.imageView_cardIcon30);
        cardIcon[31] = weatherView.findViewById(R.id.imageView_cardIcon31);
        cardIcon[32] = weatherView.findViewById(R.id.imageView_cardIcon32);
        cardIcon[33] = weatherView.findViewById(R.id.imageView_cardIcon33);
        cardIcon[34] = weatherView.findViewById(R.id.imageView_cardIcon34);
        cardIcon[35] = weatherView.findViewById(R.id.imageView_cardIcon35);
        cardIcon[36] = weatherView.findViewById(R.id.imageView_cardIcon36);
        cardIcon[37] = weatherView.findViewById(R.id.imageView_cardIcon37);
        cardIcon[38] = weatherView.findViewById(R.id.imageView_cardIcon38);
        cardIcon[39] = weatherView.findViewById(R.id.imageView_cardIcon39);
    }

    public void setupCardSummary(View weatherView) {
        cardSummary[0] = weatherView.findViewById(R.id.textView_cardSummary0);
        cardSummary[1] = weatherView.findViewById(R.id.textView_cardSummary1);
        cardSummary[2] = weatherView.findViewById(R.id.textView_cardSummary2);
        cardSummary[3] = weatherView.findViewById(R.id.textView_cardSummary3);
        cardSummary[4] = weatherView.findViewById(R.id.textView_cardSummary4);
        cardSummary[5] = weatherView.findViewById(R.id.textView_cardSummary5);
        cardSummary[6] = weatherView.findViewById(R.id.textView_cardSummary6);
        cardSummary[7] = weatherView.findViewById(R.id.textView_cardSummary7);
        cardSummary[8] = weatherView.findViewById(R.id.textView_cardSummary8);
        cardSummary[9] = weatherView.findViewById(R.id.textView_cardSummary9);
        cardSummary[10] = weatherView.findViewById(R.id.textView_cardSummary10);
        cardSummary[11] = weatherView.findViewById(R.id.textView_cardSummary11);
        cardSummary[12] = weatherView.findViewById(R.id.textView_cardSummary12);
        cardSummary[13] = weatherView.findViewById(R.id.textView_cardSummary13);
        cardSummary[14] = weatherView.findViewById(R.id.textView_cardSummary14);
        cardSummary[15] = weatherView.findViewById(R.id.textView_cardSummary15);
        cardSummary[16] = weatherView.findViewById(R.id.textView_cardSummary16);
        cardSummary[17] = weatherView.findViewById(R.id.textView_cardSummary17);
        cardSummary[18] = weatherView.findViewById(R.id.textView_cardSummary18);
        cardSummary[19] = weatherView.findViewById(R.id.textView_cardSummary19);
        cardSummary[20] = weatherView.findViewById(R.id.textView_cardSummary20);
        cardSummary[21] = weatherView.findViewById(R.id.textView_cardSummary21);
        cardSummary[22] = weatherView.findViewById(R.id.textView_cardSummary22);
        cardSummary[23] = weatherView.findViewById(R.id.textView_cardSummary23);
        cardSummary[24] = weatherView.findViewById(R.id.textView_cardSummary24);
        cardSummary[25] = weatherView.findViewById(R.id.textView_cardSummary25);
        cardSummary[26] = weatherView.findViewById(R.id.textView_cardSummary26);
        cardSummary[27] = weatherView.findViewById(R.id.textView_cardSummary27);
        cardSummary[28] = weatherView.findViewById(R.id.textView_cardSummary28);
        cardSummary[29] = weatherView.findViewById(R.id.textView_cardSummary29);
        cardSummary[30] = weatherView.findViewById(R.id.textView_cardSummary30);
        cardSummary[31] = weatherView.findViewById(R.id.textView_cardSummary31);
        cardSummary[32] = weatherView.findViewById(R.id.textView_cardSummary32);
        cardSummary[33] = weatherView.findViewById(R.id.textView_cardSummary33);
        cardSummary[34] = weatherView.findViewById(R.id.textView_cardSummary34);
        cardSummary[35] = weatherView.findViewById(R.id.textView_cardSummary35);
        cardSummary[36] = weatherView.findViewById(R.id.textView_cardSummary36);
        cardSummary[37] = weatherView.findViewById(R.id.textView_cardSummary37);
        cardSummary[38] = weatherView.findViewById(R.id.textView_cardSummary38);
        cardSummary[39] = weatherView.findViewById(R.id.textView_cardSummary39);
    }

    public void setupCardTemperature(View weatherView) {
        cardTemperature[0] = weatherView.findViewById(R.id.textView_cardTemperature0);
        cardTemperature[1] = weatherView.findViewById(R.id.textView_cardTemperature1);
        cardTemperature[2] = weatherView.findViewById(R.id.textView_cardTemperature2);
        cardTemperature[3] = weatherView.findViewById(R.id.textView_cardTemperature3);
        cardTemperature[4] = weatherView.findViewById(R.id.textView_cardTemperature4);
        cardTemperature[5] = weatherView.findViewById(R.id.textView_cardTemperature5);
        cardTemperature[6] = weatherView.findViewById(R.id.textView_cardTemperature6);
        cardTemperature[7] = weatherView.findViewById(R.id.textView_cardTemperature7);
        cardTemperature[8] = weatherView.findViewById(R.id.textView_cardTemperature8);
        cardTemperature[9] = weatherView.findViewById(R.id.textView_cardTemperature9);
        cardTemperature[10] = weatherView.findViewById(R.id.textView_cardTemperature10);
        cardTemperature[11] = weatherView.findViewById(R.id.textView_cardTemperature11);
        cardTemperature[12] = weatherView.findViewById(R.id.textView_cardTemperature12);
        cardTemperature[13] = weatherView.findViewById(R.id.textView_cardTemperature13);
        cardTemperature[14] = weatherView.findViewById(R.id.textView_cardTemperature14);
        cardTemperature[15] = weatherView.findViewById(R.id.textView_cardTemperature15);
        cardTemperature[16] = weatherView.findViewById(R.id.textView_cardTemperature16);
        cardTemperature[17] = weatherView.findViewById(R.id.textView_cardTemperature17);
        cardTemperature[18] = weatherView.findViewById(R.id.textView_cardTemperature18);
        cardTemperature[19] = weatherView.findViewById(R.id.textView_cardTemperature19);
        cardTemperature[20] = weatherView.findViewById(R.id.textView_cardTemperature20);
        cardTemperature[21] = weatherView.findViewById(R.id.textView_cardTemperature21);
        cardTemperature[22] = weatherView.findViewById(R.id.textView_cardTemperature22);
        cardTemperature[23] = weatherView.findViewById(R.id.textView_cardTemperature23);
        cardTemperature[24] = weatherView.findViewById(R.id.textView_cardTemperature24);
        cardTemperature[25] = weatherView.findViewById(R.id.textView_cardTemperature25);
        cardTemperature[26] = weatherView.findViewById(R.id.textView_cardTemperature26);
        cardTemperature[27] = weatherView.findViewById(R.id.textView_cardTemperature27);
        cardTemperature[28] = weatherView.findViewById(R.id.textView_cardTemperature28);
        cardTemperature[29] = weatherView.findViewById(R.id.textView_cardTemperature29);
        cardTemperature[30] = weatherView.findViewById(R.id.textView_cardTemperature30);
        cardTemperature[31] = weatherView.findViewById(R.id.textView_cardTemperature31);
        cardTemperature[32] = weatherView.findViewById(R.id.textView_cardTemperature32);
        cardTemperature[33] = weatherView.findViewById(R.id.textView_cardTemperature33);
        cardTemperature[34] = weatherView.findViewById(R.id.textView_cardTemperature34);
        cardTemperature[35] = weatherView.findViewById(R.id.textView_cardTemperature35);
        cardTemperature[36] = weatherView.findViewById(R.id.textView_cardTemperature36);
        cardTemperature[37] = weatherView.findViewById(R.id.textView_cardTemperature37);
        cardTemperature[38] = weatherView.findViewById(R.id.textView_cardTemperature38);
        cardTemperature[39] = weatherView.findViewById(R.id.textView_cardTemperature39);
    }

    public void setupCardDate(View weatherView) {
        cardDate[0] = weatherView.findViewById(R.id.textView_cardDate0);
        cardDate[1] = weatherView.findViewById(R.id.textView_cardDate1);
        cardDate[2] = weatherView.findViewById(R.id.textView_cardDate2);
        cardDate[3] = weatherView.findViewById(R.id.textView_cardDate3);
        cardDate[4] = weatherView.findViewById(R.id.textView_cardDate4);
        cardDate[5] = weatherView.findViewById(R.id.textView_cardDate5);
        cardDate[6] = weatherView.findViewById(R.id.textView_cardDate6);
        cardDate[7] = weatherView.findViewById(R.id.textView_cardDate7);
        cardDate[8] = weatherView.findViewById(R.id.textView_cardDate8);
        cardDate[9] = weatherView.findViewById(R.id.textView_cardDate9);
        cardDate[10] = weatherView.findViewById(R.id.textView_cardDate10);
        cardDate[11] = weatherView.findViewById(R.id.textView_cardDate11);
        cardDate[12] = weatherView.findViewById(R.id.textView_cardDate12);
        cardDate[13] = weatherView.findViewById(R.id.textView_cardDate13);
        cardDate[14] = weatherView.findViewById(R.id.textView_cardDate14);
        cardDate[15] = weatherView.findViewById(R.id.textView_cardDate15);
        cardDate[16] = weatherView.findViewById(R.id.textView_cardDate16);
        cardDate[17] = weatherView.findViewById(R.id.textView_cardDate17);
        cardDate[18] = weatherView.findViewById(R.id.textView_cardDate18);
        cardDate[19] = weatherView.findViewById(R.id.textView_cardDate19);
        cardDate[20] = weatherView.findViewById(R.id.textView_cardDate20);
        cardDate[21] = weatherView.findViewById(R.id.textView_cardDate21);
        cardDate[22] = weatherView.findViewById(R.id.textView_cardDate22);
        cardDate[23] = weatherView.findViewById(R.id.textView_cardDate23);
        cardDate[24] = weatherView.findViewById(R.id.textView_cardDate24);
        cardDate[25] = weatherView.findViewById(R.id.textView_cardDate25);
        cardDate[26] = weatherView.findViewById(R.id.textView_cardDate26);
        cardDate[27] = weatherView.findViewById(R.id.textView_cardDate27);
        cardDate[28] = weatherView.findViewById(R.id.textView_cardDate28);
        cardDate[29] = weatherView.findViewById(R.id.textView_cardDate29);
        cardDate[30] = weatherView.findViewById(R.id.textView_cardDate30);
        cardDate[31] = weatherView.findViewById(R.id.textView_cardDate31);
        cardDate[32] = weatherView.findViewById(R.id.textView_cardDate32);
        cardDate[33] = weatherView.findViewById(R.id.textView_cardDate33);
        cardDate[34] = weatherView.findViewById(R.id.textView_cardDate34);
        cardDate[35] = weatherView.findViewById(R.id.textView_cardDate35);
        cardDate[36] = weatherView.findViewById(R.id.textView_cardDate36);
        cardDate[37] = weatherView.findViewById(R.id.textView_cardDate37);
        cardDate[38] = weatherView.findViewById(R.id.textView_cardDate38);
        cardDate[39] = weatherView.findViewById(R.id.textView_cardDate39);

        for(int dateNum = 0; dateNum < 40; dateNum++)
        {
            cardDate[dateNum].setTypeface(Typeface.DEFAULT_BOLD);
        }
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
        weatherStatus.setText("Weather Status: "+ weatherMain + "\n"+
                "Temperature: "+ temp + "\n" +
                "Feels Like: " + feels_like + "\n" +
                "Max Temperature: " + temp_max + "\n" + "Minimum Temperature" + temp_min + "\n" +
                "Wind Speed: " + windSpeed + "\n" + "Wind Degree:" + windDeg + "\n" +
                "Pressure: " + pressure + "\n" + "Humidity: " + humidity);
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
            week_cod = object.getString("cod");
            week_cnt = object.getInt("cnt");
            JSONObject city = object.getJSONObject("city");


            week_locationName = city.getString("name");
            JSONObject coord = city.getJSONObject("coord");
            week_latitude = coord.getDouble("lat");
            week_longitude = coord.getDouble("lon");
            week_country = city.getString("country");
            week_timezone = city.getInt("timezone");
            week_sunrise = city.getInt("sunrise");
            week_sunset = city.getInt("sunset");

            week_dt = new int[week_cnt];
            week_date = new String[week_cnt];
            week_temp = new int[week_cnt];
            week_feelslike = new int[week_cnt];
            week_temp_min = new int[week_cnt];
            week_temp_max = new int[week_cnt];
            week_pressure = new int[week_cnt];
            week_sea_level = new int[week_cnt];
            week_grnd_level = new int[week_cnt];
            week_humidity = new int[week_cnt];
            week_temp_kf = new int[week_cnt];
            week_clouds_all = new int[week_cnt];
            week_weather_id = new int[week_cnt];
            week_weather_main = new String[week_cnt];
            week_weather_desc = new String[week_cnt];
            week_weather_icon = new String[week_cnt];
            week_wind_speed = new double[week_cnt];
            week_wind_deg = new double[week_cnt];
            week_sys_pod = new String[week_cnt];


            for(int i = 0; i < 40; i++)
            {
                JSONArray listArray = object.getJSONArray("list");
                JSONObject listItem = listArray.getJSONObject(i);
                week_dt[i] = listItem.getInt("dt");

                JSONObject main = listItem.getJSONObject("main");
                week_temp[i] = main.getInt("temp");
                week_feelslike[i] = main.getInt("feels_like");

                week_temp_min[i] = main.getInt("temp_min");
                week_temp_max[i] = main.getInt("temp_max");
                week_pressure[i] = main.getInt("pressure");
                week_sea_level[i] = main.getInt("sea_level");
                week_grnd_level[i] = main.getInt("grnd_level");
                week_humidity[i] = main.getInt("humidity");
                week_temp_kf[i] = main.getInt("temp_kf");


                JSONObject weather = listItem.getJSONArray("weather").getJSONObject(0);
                week_weather_id[i] = weather.getInt("id");
                week_weather_main[i] = weather.getString("main");
                week_weather_desc[i] = weather.getString("description");
                week_weather_icon[i] = weather.getString("icon");

                JSONObject clouds = listItem.getJSONObject("clouds");
                week_clouds_all[i] = clouds.getInt("all");

                JSONObject wind = listItem.getJSONObject("wind");
                week_wind_speed[i] = wind.getDouble("speed");
                week_wind_deg[i] = wind.getDouble("deg");



                JSONObject sys = listItem.getJSONObject("sys");
                week_sys_pod[i] = sys.getString("pod");
                if(i == 0)
                {
                    Toast.makeText(getContext(),"POD 0 is: " + week_sys_pod[i],Toast.LENGTH_SHORT).show();
                }

                week_date[i] = listItem.getString("dt_txt");
            }

        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Error", e.getMessage());
        }


        ///////////////////////////////////////////////////////////////////////////////////////////
        /* This try catch block handles the rain object in the JSON response.
         Since this value is not always given when a client queries the API a separate block needed
         to be made to handle it while not disrupting the queries to the other values.
         Objects such as this rain object require their own block.
         Another object like this one is the snow object*/
        try {
            JSONObject dynamicValuesObject = (JSONObject) new JSONTokener(response).nextValue();
            week_rain3h = new int[week_cnt];

            for(int i = 0; i < 40; i++) {
                JSONArray listArray = dynamicValuesObject.getJSONArray("list");
                JSONObject listItem = listArray.getJSONObject(i);
                JSONObject rain = listItem.getJSONObject("rain");
                week_rain3h[i] = rain.getInt("3h");
            }
        ///////////////////////////////////////////////////////////////////////////////////////////

        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Error", e.getMessage());
        }


        //TODO Need to add conditions for Rain Volume and Snow Volume
        for(int k = 0; k < 40; k++)
        {
            cardDate[k].setText(week_date[k]);
            cardTemperature[k].setText(week_temp[k] + "°" );
            cardSummary[k].setText(week_weather_desc[k] + "\n"
            + "Feels Like: " + week_feelslike[k] + "°" + "\n"
            + "Max Temperature: " + week_temp_max[k] + "°" + "\n"
            + "Min Temperature: " + week_temp_min[k] + "°" + "\n"
            + "Wind Speed: " + week_wind_speed[k] + " " + windDirection(week_wind_deg[k]) + "\n"
            + "Humidity: " + week_humidity[k] + "\n"
            + "Pressure: " + week_pressure[k] + "\n"
            + "Sunrise: " + week_sunrise + "\n"
            + "Sunset: " + week_sunset);
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
            //fiveDayHeader.setText(response);
            //TODO Need to deal with Icons Here
            for(int c = 0; c < 40; c++)
            {
                new RetrieveWeatherIconTask(cardIcon[c]).execute(API_ICON_URL+week_weather_icon[c]+"@2x.png");
            }

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
