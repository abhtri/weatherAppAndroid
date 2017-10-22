package com.example.abhishektripathi.weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.abhishektripathi.weather.webservice.AppController;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    String weatherWebserviceURL = "http://api.openweathermap.org/data/2.5/weather" ;//?q=Hyderabad,IN&appid=10d5414f3680d2f4061949295552471d&units=metric";
    //the loading Dialog
    ProgressDialog pDialog;
    // Textview to show temperature and description
    TextView city,temperature, description;
    // background image
    ImageView weatherBackground;
    // JSON object that contains weather information
    JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = (TextView)findViewById(R.id.city);
        temperature = (TextView) findViewById(R.id.temperature);
        description = (TextView) findViewById(R.id.description);
        weatherBackground = (ImageView) findViewById(R.id.weatherbackground);


        // prepare the loading Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait while retrieving the weather condition ...");
        pDialog.setCancelable(false);

        if (!isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, "Please check your Internet connection", Toast.LENGTH_LONG).show();
        }/* else {

        }*/



    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void SendMessage(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        final String message = editText.getText().toString();

        // whole else method


            pDialog.show();
            String weatherWebserviceURL1 = weatherWebserviceURL +"?q="+message.trim()+",IN&appid=10d5414f3680d2f4061949295552471d&units=metric";
            // make HTTP request to retrieve the weather
        System.out.println(weatherWebserviceURL1);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    weatherWebserviceURL1, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Parsing json object response
                        // response will be a json object


                        jsonObj = (JSONObject) response.getJSONArray("weather").get(0);
                        // display weather description into the "description textview"
                        description.clearComposingText();
                        description.setText(jsonObj.getString("description"));
                        // display the temperature
                        temperature.clearComposingText();
                        temperature.setText(response.getJSONObject("main").getString("temp") + " °C");
                        city.setText(message);
                        Log.d("info",response.getJSONObject("main").getString("temp"));
                        System.out.println(response.getJSONObject("main").getString("temp"));
                        String backgroundImage = "";

                        //choose the image to set as background according to weather condition
                        if (jsonObj.getString("main").equals("Clouds")) {
                            backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/clouds-wallpaper2.jpg";
                        } else if (jsonObj.getString("main").equals("Rain")) {
                            backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/rainy-wallpaper1.jpg";
                        } else if (jsonObj.getString("main").equals("Snow")) {
                            backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/snow-wallpaper1.jpg";
                        } else{
                            backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/snow-wallpaper1.jpg";
                        }

                        // load image from link and display it on background
                        // We'll use the Glide library
                        Glide
                                .with(getApplicationContext())
                                .load(backgroundImage)
                                .centerCrop()
                                .crossFade()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        System.out.println(e.toString());
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(weatherBackground);

                        // hide the loading Dialog
                        pDialog.dismiss();
                        pDialog.hide();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error , try again ! ", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                    // hide the progress dialog
                    pDialog.dismiss();
                }
            });
            AppController.getInstance(this).addToRequestQueue(jsonObjReq);


    }
}
