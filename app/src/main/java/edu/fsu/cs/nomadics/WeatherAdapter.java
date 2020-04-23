package edu.fsu.cs.nomadics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private JSONObject object;
    private boolean imperialUnits;
    String tempUnits;
    String windUnits;
    final int CardItemCount = 40;
    String API_ICON_URL = " http://openweathermap.org/img/wn/";

    int week_cnt;
    String week_cod;
    String week_locationName;
    String week_country;
    double week_latitude;
    double week_longitude;
    String week_sunrise;
    String week_sunset;
    int week_timezone;
    int week_dt;
    int week_temp;
    int week_feelslike;
    int week_temp_min;
    int week_temp_max;
    int week_pressure;
    int week_sea_level;
    int week_grnd_level;
    int week_humidity;
    int week_temp_kf;
    int week_weather_id;
    String week_weather_main;
    String week_weather_desc;
    String week_weather_icon;
    int week_clouds_all;
    double week_wind_speed;
    double week_wind_deg;
    int week_rain3h; // Rain Volume for last 3 hours, units: mm
    int week_snow3h; // Snow Volume for last 3 hours
    String week_sys_pod;
    String week_date;


    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        public ImageView cardIcon;
        public TextView cardSummary;
        public TextView cardTemperature;
        public TextView cardDate;

        //--------------------------5 Day Weather JSON variables


        public WeatherViewHolder(View v){
            super(v);
            layout = v;
            cardIcon = v.findViewById(R.id.imageView_cardIcon0);
            cardSummary = v.findViewById(R.id.textView_cardSummary0);
            cardTemperature = v.findViewById(R.id.textView_cardTemperature0);
            cardDate = v.findViewById(R.id.textView_cardDate0);
            cardDate.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public WeatherAdapter(JSONObject mainObject, boolean isImperial)
    {
        object = mainObject;
        imperialUnits = isImperial;
        if(isImperial)
        {
            tempUnits = "F";
            windUnits = " miles/hour";
        }
        else
        {
            tempUnits = "C";
            windUnits = " meters/second";
        }
    }

    @Override
    public WeatherAdapter.WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.weather_card_layout,parent,false);

        WeatherAdapter.WeatherViewHolder vh = new WeatherViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {

        try {
            // If a value in here comes up as null or 0 chances are the some value failed to init.


            week_cod = object.getString("cod");
            week_cnt = object.getInt("cnt");
            JSONObject city = object.getJSONObject("city");


            week_locationName = city.getString("name");
            JSONObject coord = city.getJSONObject("coord");
            week_latitude = coord.getDouble("lat");
            week_longitude = coord.getDouble("lon");
            week_country = city.getString("country");
            week_timezone = city.getInt("timezone");
            week_sunrise = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy").format(new Date(city.getInt("sunrise")* 1000L));
            week_sunset = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy").format(new Date(city.getInt("sunset")* 1000L));


            JSONArray listArray = object.getJSONArray("list");
            JSONObject listItem = listArray.getJSONObject(position);
            week_dt = listItem.getInt("dt");

            JSONObject main = listItem.getJSONObject("main");
            week_temp = main.getInt("temp");
            week_feelslike = main.getInt("feels_like");

            week_temp_min = main.getInt("temp_min");
            week_temp_max = main.getInt("temp_max");
            week_pressure = main.getInt("pressure");
            week_sea_level = main.getInt("sea_level");
            week_grnd_level = main.getInt("grnd_level");
            week_humidity = main.getInt("humidity");
            week_temp_kf = main.getInt("temp_kf");


            JSONObject weather = listItem.getJSONArray("weather").getJSONObject(0);
            week_weather_id = weather.getInt("id");
            week_weather_main = weather.getString("main");
            week_weather_desc = weather.getString("description");
            week_weather_icon = weather.getString("icon");

            JSONObject clouds = listItem.getJSONObject("clouds");
            week_clouds_all = clouds.getInt("all");

            JSONObject wind = listItem.getJSONObject("wind");
            week_wind_speed = wind.getDouble("speed");
            week_wind_deg = wind.getDouble("deg");



            JSONObject sys = listItem.getJSONObject("sys");
            week_sys_pod = sys.getString("pod");



            week_date = listItem.getString("dt_txt");


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
            JSONObject dynamicValuesObject = object;
            //week_rain3h = new int[week_cnt];

            JSONArray listArray = dynamicValuesObject.getJSONArray("list");
            JSONObject listItem = listArray.getJSONObject(position);
            JSONObject rain = listItem.getJSONObject("rain");
            week_rain3h = rain.getInt("3h");
            ///////////////////////////////////////////////////////////////////////////////////////////

        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Error", e.getMessage());
        }


        new RetrieveWeatherIconTask(holder.cardIcon).execute(API_ICON_URL+week_weather_icon+"@2x.png");
        holder.cardDate.setText(week_date);
        holder.cardTemperature.setText(week_temp + "°" );
        holder.cardSummary.setText(week_weather_desc + "\n"
                + "Feels Like: " + week_feelslike + "°" + tempUnits + "\n"
                + "Max Temperature: " + week_temp_max + "°" + tempUnits + "\n"
                + "Min Temperature: " + week_temp_min + "°" +tempUnits + "\n"
                + "Wind Speed: " + week_wind_speed + windUnits +" " + windDirection(week_wind_deg) + "\n"
                + "Humidity: " + week_humidity + "%" + "\n"
                + "Pressure: " + week_pressure + " hPa" + "\n"
                + "Sunrise: " + week_sunrise + "\n"
                + "Sunset: " + week_sunset);
    }

    @Override
    public int getItemCount()
    {
        return CardItemCount;
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
