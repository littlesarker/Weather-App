package my.app.weathercasting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AirQualityActivity extends AppCompatActivity {


    TextView pm10, pm2,carbon,sulphur,dust;
    float latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        pm10 = findViewById(R.id.resID);
        pm2 = findViewById(R.id.pm2ID);

        Bundle bundle = getIntent().getExtras();
        latitude = Float.parseFloat(bundle.getString("lati"));
        longitude = Float.parseFloat(bundle.getString("long"));

        carbon=findViewById(R.id.carbonMonoxideID);
        sulphur=findViewById(R.id.sulferoxideID);
        dust=findViewById(R.id.dustID);


        getData();


    }

    public void getData() {
        ImageView img = (ImageView) findViewById(R.id.maskedID);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String myUrl = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude="+latitude+"&longitude="+longitude+"&hourly=pm10,pm2_5,carbon_monoxide,sulphur_dioxide,dust";
        StringRequest myRequest = new StringRequest(Request.Method.GET, myUrl, response -> {

            try {
                //Create a JSON object containing information from the API.
                JSONObject jsonObject = new JSONObject(response);

                JSONArray array = jsonObject.getJSONObject("hourly").getJSONArray("pm10");
                Double inde = (Double) array.get(2);
                String str = Double.toString(inde);
                pm10.setText(str+" μg/m³");

                JSONArray array2 = jsonObject.getJSONObject("hourly").getJSONArray("pm2_5");
                Double dpm2 = (Double) array2.get(2);
                String str2 = Double.toString(dpm2);
                pm2.setText(str2 + " μg/m³");

                JSONArray array3 = jsonObject.getJSONObject("hourly").getJSONArray("carbon_monoxide");
                Double cmx = (Double) array3.get(10);
                String strcarbon = Double.toString(cmx);
                carbon.setText("Carbon "+strcarbon + " μg/m³");


                JSONArray array4 = jsonObject.getJSONObject("hourly").getJSONArray("sulphur_dioxide");
                Double sul = (Double) array4.get(10);
                String strsul = Double.toString(sul);
                sulphur.setText("Sulphur "+strsul + " μg/m³");

                JSONArray array5 = jsonObject.getJSONObject("hourly").getJSONArray("dust");
                Double dst = (Double) array5.get(10);
                String strdust = Double.toString(dst);
                dust.setText("Dust "+strdust + " μg/m³");


                float f = Float.parseFloat(str);
                float ff = Float.parseFloat(str2);
                if (f > 54 || ff > 12) {
                    img.setImageResource(R.drawable.mmask);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                volleyError -> Toast.makeText(AirQualityActivity.this, volleyError.getMessage(),
                        Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(myRequest);


    }
}