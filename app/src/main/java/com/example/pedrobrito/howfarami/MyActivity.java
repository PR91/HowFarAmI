package com.example.pedrobrito.howfarami;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MyActivity extends Activity implements SensorEventListener {

    TextView distanceInfo;
    TextView bearingInfo;
    TextView applicationTitle;
    TextView distanceLable;
    TextView distanceText;
    ImageView rightArrow;
    ImageView leftArrow;

    int azimuthRef;
    int azimuthSen;
    int azimuthSum;
    char home;

    int vibrate;

    Sensor s;
    SensorManager sm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Typeface robotoThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        distanceInfo = (TextView) findViewById(R.id.distanceInfo);
        bearingInfo = (TextView) findViewById(R.id.bearingInfo);
        distanceLable = (TextView) findViewById(R.id.distanceLabel);
        applicationTitle = (TextView) findViewById(R.id.applicationTitle);
        distanceText = (TextView) findViewById(R.id.distanceText);

        rightArrow = (ImageView) findViewById(R.id.imageViewRight);
        leftArrow = (ImageView) findViewById(R.id.imageViewLeft);

        distanceInfo.setTypeface(robotoThin);
        applicationTitle.setTypeface(robotoLight);
        distanceLable.setTypeface(robotoLight);
        distanceText.setTypeface(robotoLight);
        bearingInfo.setTypeface(robotoLight);


        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new mylocationlistener();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Choosing Sensor Type
        s = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //Activating The Sensor Listener
        sm.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST);

        if (mWifi.isConnected())
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
        else
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (home == 0) {

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            azimuthSen = Math.round(sensorEvent.values[0]);

            azimuthSum = azimuthRef - azimuthSen;

            if (azimuthSum < 0)
                azimuthSum = azimuthSum + 360;

            if (azimuthSum > 180)
                azimuthSum = azimuthSum - 360;

            if (azimuthSum > -5 && azimuthSum < 5) {
                //  bearingInfo.setTextColor(0xFFFFFFFF);
                bearingInfo.setAlpha(1F);
                leftArrow.setAlpha(0.2F);
                rightArrow.setAlpha(0.2F);

                if (vibrate == 1) {
                    v.vibrate(20);
                    vibrate = 0;
                }
            } else {
                bearingInfo.setAlpha(0.2F);
                vibrate = 1;

                if (azimuthSum < 5) {
                    leftArrow.setAlpha(1F);
                    rightArrow.setAlpha(0.2F);
                } else {
                    leftArrow.setAlpha(0.2F);
                    rightArrow.setAlpha(1F);
                }


            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    class mylocationlistener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {

                double lon2 = location.getLongitude();
                double lat2 = location.getLatitude();

            //Casa Bento
               // double lon1 = -8.581369;
               // double lat1 = 41.172890;

            //Casa Brito
                double lon1 = -8.59425260;
                double lat1 = 41.1533801;

                Location loc1 = new Location("");
                loc1.setLatitude(lat1);
                loc1.setLongitude(lon1);

                Location loc2 = new Location("");
                loc2.setLatitude(lat2);
                loc2.setLongitude(lon2);

                double distance = loc2.distanceTo(loc1);
                int bearingInDegrees = Math.round(loc2.bearingTo(loc1));
                int distanceInMeters;

                if (distance < 15){
                    home = 1;

                    distanceLable.setVisibility(View.INVISIBLE);
                    rightArrow.setVisibility(View.INVISIBLE);
                    leftArrow.setVisibility(View.INVISIBLE);

                    bearingInfo.setAlpha(1F);
                    distanceInfo.setText("@");

                }

                else {

                    home = 0;

                    distanceLable.setVisibility(View.VISIBLE);
                    rightArrow.setVisibility(View.VISIBLE);
                    leftArrow.setVisibility(View.VISIBLE);

                    if (distance < 1000) {
                        distanceInMeters = (int) distance;
                        distanceLable.setText("meters away");
                        distanceInfo.setText("" + String.valueOf(distanceInMeters));
                    } else if (distance > 1000 && distance < 100000) {
                        distance = Math.round(distance / 100) / 10.0;
                        distanceLable.setText("kilometers away");
                        distanceInfo.setText("" + String.valueOf(distance));
                    } else {
                        distance = distance / 1000;
                        distanceInMeters = (int) distance;
                        distanceInfo.setText("" + String.valueOf(distanceInMeters));
                        distanceLable.setText("kilometers away");
                    }
                }
                //bearingInDegrees from [-180, 180] to [0, 360]

                if (bearingInDegrees < 0)
                    azimuthRef = 360 + bearingInDegrees;
                else
                    azimuthRef = bearingInDegrees;
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}