package my.app.weathercasting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    TextView tempre, isDay, windspeed, winddirection, weatherCode;
    String latitude = "", longitude = "";
    AlertDialog.Builder builder;
    Button airButton;
    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        builder = new AlertDialog.Builder(this);

        tempre = findViewById(R.id.tempID);
        windspeed = findViewById(R.id.speedID);
        winddirection = findViewById(R.id.windDID);
        isDay = findViewById(R.id.isDayID);
        weatherCode = findViewById(R.id.weatherCodeID);
        airButton = findViewById(R.id.airbuttonID);


        if (isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_LONG).show();

            gpsTracker = new GpsTracker(MainActivity.this);
            if (gpsTracker.canGetLocation()) {
                double dla = gpsTracker.getLatitude();
                double dlo = gpsTracker.getLongitude();

                latitude = String.valueOf(dla);
                longitude = String.valueOf(dlo);
                Toast.makeText(getApplicationContext(), latitude + " " + longitude, Toast.LENGTH_LONG).show();
            } else {
                gpsTracker.showSettingsAlert();
            }

            getData();

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            checkIn();
            airButton.setEnabled(false);

            //end of else
        }

        onclick();

    }

    public void getData() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String myUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true&,windspeed_10m";
        StringRequest myRequest = new StringRequest(Request.Method.GET, myUrl, response -> {
            try {
                //Create a JSON object containing information from the API.
                JSONObject myJsonObject = new JSONObject(response);
                String temp = myJsonObject.getJSONObject("current_weather").getString("temperature");
                String ws = myJsonObject.getJSONObject("current_weather").getString("windspeed");
                String wD = myJsonObject.getJSONObject("current_weather").getString("winddirection");
                String wC = myJsonObject.getJSONObject("current_weather").getString("weathercode");
                String isday = myJsonObject.getJSONObject("current_weather").getString("is_day");


                tempre.setText(temp + "\u2103");
                windspeed.setText(ws + " Kmh");

                float number = Float.valueOf(wD).intValue();
                String Direction = windDirection(number);

                winddirection.setText(Direction);


                if (isday == "1") {
                    isDay.setText("Day");
                    wallpaper(isday);
                } else {
                    isDay.setText("Night");
                    wallpaper(isday);
                }

                int num = Integer.parseInt(wC);
                dataArray(num);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> Toast.makeText(MainActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(myRequest);

    }

    public void wallpaper(String s) {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.MainlayoutID);

        if (s == "0") {
            layout.setBackgroundResource(R.drawable.back2);
        }
        if (s == "1") {
            layout.setBackgroundResource(R.drawable.back3);
        }

    }

    public String windDirection(float directionInDegrees) {
        String cardinalDirection = null;
        if ((directionInDegrees >= 348.75) && (directionInDegrees <= 360) || (directionInDegrees >= 0) && (directionInDegrees <= 11.25)) {
            cardinalDirection = "North";
        } else if ((directionInDegrees >= 11.25) && (directionInDegrees <= 33.75)) {
            cardinalDirection = "North-northeast";
        } else if ((directionInDegrees >= 33.75) && (directionInDegrees <= 56.25)) {
            cardinalDirection = "NE";
        } else if ((directionInDegrees >= 56.25) && (directionInDegrees <= 78.75)) {
            cardinalDirection = "East-northeast";
        } else if ((directionInDegrees >= 78.75) && (directionInDegrees <= 101.25)) {
            cardinalDirection = "East";
        } else if ((directionInDegrees >= 101.25) && (directionInDegrees <= 123.75)) {
            cardinalDirection = "East-southeast";
        } else if ((directionInDegrees >= 123.75) && (directionInDegrees <= 146.25)) {
            cardinalDirection = "SE";
        } else if ((directionInDegrees >= 146.25) && (directionInDegrees <= 168.75)) {
            cardinalDirection = "South-southeast ";
        } else if ((directionInDegrees >= 168.75) && (directionInDegrees <= 191.25)) {
            cardinalDirection = "South";
        } else if ((directionInDegrees >= 191.25) && (directionInDegrees <= 213.75)) {
            cardinalDirection = "South-southwest";
        } else if ((directionInDegrees >= 213.75) && (directionInDegrees <= 236.25)) {
            cardinalDirection = "SW";
        } else if ((directionInDegrees >= 236.25) && (directionInDegrees <= 258.75)) {
            cardinalDirection = "West-southwest";
        } else if ((directionInDegrees >= 258.75) && (directionInDegrees <= 281.25)) {
            cardinalDirection = "West";
        } else if ((directionInDegrees >= 281.25) && (directionInDegrees <= 303.75)) {
            cardinalDirection = "West-northwest";
        } else if ((directionInDegrees >= 303.75) && (directionInDegrees <= 326.25)) {
            cardinalDirection = "NW";
        } else if ((directionInDegrees >= 326.25) && (directionInDegrees <= 348.75)) {
            cardinalDirection = "North-northwest";
        } else {
            cardinalDirection = "?";
        }

        return cardinalDirection;
    }


    public void dataArray(int n) {
        ArrayList<String> descriptionWeather = new ArrayList<String>(Arrays.asList("Cloud development not observed or not observable", "Cloud generally dissolving or becoming less developed", "State of sky on the whole unchanged", "Clouds generally forming or developing", "Visibility reduced by smoke", "Haze", "Widespread dust in suspension in the air", "Dust or sand raised by wind at or near the station", "Well-developed dust or sand whirl(s)", "Dust-storm or sandstorm within sight or at the station", "Mist", "Patches of shallow fog or ice fog at the station", "More or less continuous shallow fog or ice fog at the station", "Lightning visible, no thunder heard", "Precipitation within sight, not reaching the ground", "Precipitation within sight, reaching the ground > 5 km from the station", "Precipitation within sight near to, but not at the station", "Thunderstorm, but no precipitation", "Squalls at or within sight of the station", "Funnel clouds at or within sight of the station", "Drizzle (not freezing) or snow grains, not falling as showers", "Rain (not freezing), not falling as showers", "Snow, not falling as showers", "Rain and snow or ice pellets, not falling as showers", "Freezing drizzle or freezing rain", "Shower(s) of rain", "Shower(s) of snow, or of rain and snow", "Shower(s) of hail, or of rain and hail", "Fog or ice fog", "Thunderstorm (with or without precipitation)", "Slight or moderate dust-storm or sandstorm has decreased", "Slight or moderate dust-storm or sandstorm no appreciable change", "Slight or moderate dust-storm or sandstorm - has begun or has increased", "Severe dust-storm or sandstorm has decreased", "Severe dust-storm or sandstorm no appreciable change", "Severe dust-storm or sandstorm has begun or has increased", "Slight/moderate drifting snow generally low (below eye level)", "Heavy drifting snow generally low (below eye level)", "Slight/moderate blowing snow  generally high (above eye level)", "Heavy blowing snow - generally high (above eye level)", "Fog or ice fog at distance but not at station", "Fog or ice fog in patches", "Fog/ice fog, sky visible, has become thinner", "Fog/ice fog, sky invisible, has become thinner", "Fog or ice fog, sky visible, no appreciable change", "Fog or ice fog, sky invisible, no appreciable change", "Fog or ice fog, sky visible, has begun or has become thicker", "Fog or ice fog, sky invisible, has begun or has become thicker", "Fog, depositing rime, sky visible", "Fog, depositing rime, sky invisible", "Drizzle not freezing intermittent slight", "Drizzle not freezing continuous slight", "Drizzle not freezing intermittent moderate", "Drizzle not freezing continuous moderate", "Drizzle not freezing intermittent heavy", "Drizzle not freezing continuous heavy", "Drizzle freezing slight", "Drizzle freezing moderate or heavy (dense)", "Rain and drizzle slight", "Rain and drizzle moderate or heavy", "Rain, not freezing, intermittent, slight", "Rain, not freezing, continuous, slight", "Rain, not freezing, intermittent, moderate", "Rain, not freezing, continuous, moderate", "Rain, not freezing, intermittent, heavy", "Rain, not freezing, continuous, heavy", "Rain, freezing, slight", "Rain, freezing, moderate or heavy", "Rain or drizzle and snow, slight", "Rain or drizzle and snow, moderate or heavy", "Intermittent fall of snowflakes, slight", "Continuous fall of snowflakes, slight", "Intermittent fall of snowflakes, moderate", "Continuous fall of snowflakes, moderate", "Intermittent fall of snowflakes, heavy", "Continuous fall of snowflakes, heavy", "Diamond dust (with or without fog)", "Snow grains (with or without fog)", "Isolated star-like snow crystals (with or without fog)", "Ice pellets", "Rain shower(s), slight", "Rain shower(s), moderate or heavy", "Rain shower(s), violent", "Shower(s) of rain and snow, slight", "Shower(s) of rain and snow, moderate or heavy", "Snow shower(s), slight", "Snow shower(s), moderate or heavy", "Shower(s) of snow pellets or small hail, slight", "Shower(s) of snow pellets or small hail, moderate or heavy", "Shower(s) of hail no thunder, slight", "Shower(s) of hail no thunder, moderate or heavy", "Slight rain - Thunderstorm during the preceding hour", "Moderate or heavy rain - Thunderstorm during the preceding hour", "Slight snow, or rain and snow mixed or hail, Thunderstorm during the preceding hour", "Moderate or heavy snow, or rain and snow mixed or hail, Thunderstorm during the preceding hour", "Thunderstorm, slight or moderate, without hail, but with rain and/or snow", "Thunderstorm, slight or moderate, with hail", "Thunderstorm, heavy, without hail, but with rain and/or snow", "Thunderstorm combined with dust/sandstorm", "Thunderstorm, heavy with hail", ""));
        String descriptWeather = descriptionWeather.get(n);
        weatherCode.setText(descriptWeather);

    }

    public void onclick() {
        airButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AirQualityActivity.class);
                intent.putExtra("lati", latitude);
                intent.putExtra("long", longitude);
                startActivity(intent);


            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void checkIn() {
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Do you want to exit ?");
        // Set Alert Title
        builder.setTitle("No Internet Connection!");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            finish();
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }
}

