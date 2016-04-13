package com.srms.psu.srmspublicapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.srms.psu.srmspublicapp.model.AdvisoryBean;
import com.srms.psu.srmspublicapp.model.ConnectionInfo;
import com.srms.psu.srmspublicapp.model.RescueRequestBean;
import com.srms.psu.srmspublicapp.service.httpservice.CheckConnectionToServer;
import com.srms.psu.srmspublicapp.service.httpservice.CheckForAdvisory;
import com.srms.psu.srmspublicapp.service.httpservice.CheckRequestStatus;
import com.srms.psu.srmspublicapp.service.httpservice.SendRescueRequestService;
import com.srms.psu.srmspublicapp.service.httpservice.UpdateRescueRequestService;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements LocationListener{

    private GoogleMap googleMap;

    private ImageButton btnCall;
    private ImageButton btnRequest;
    private Button btnSend;

    private EditText etContactNo;
    private EditText etNote;

    private boolean canConnectToServer = false;

    private String location;

    private String webServiceUrl;

    private  Context context = this;

    private ProgressDialog progressDialog;

    private String android_id;

    private RescueRequestBean rescueRequestBean = null;

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ON_GOING = "ON-GOING";
    private static final String STATUS_RESOLVED = "RESOLVED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webServiceUrl = getResources().getString(R.string.web_service_url);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();//imei



        checkConnectionToServer();


        btnCall = (ImageButton) findViewById(R.id.imgBtnCall);
        btnRequest = (ImageButton) findViewById(R.id.imgBtnRequest);

        View v = getLayoutInflater().inflate(R.layout.rescue_request, null);




        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse(getResources().getString(R.string.mdrmmc_hotline));
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });





        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available

            // Getting GoogleMap object from the fragment
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);


            Location location = null;
            Location networkLocation = null;


            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
            if(locationManager != null){
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
            if(locationManager != null){
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }


            if(networkLocation != null && location != null && networkLocation.getTime() > location.getTime()){
                location = networkLocation;
            }


            if(location!=null){

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));

                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }

        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error: " + e,
                    Toast.LENGTH_LONG).show();
        }
        if(off==0){
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        }

        setUpRequestButton();

        checkForAdvisoryUpdate();
    }

    private void checkConnectionToServer(){
        CheckConnectionToServer checkConnectionToServer = new CheckConnectionToServer();
        checkConnectionToServer.setContext(context);
        checkConnectionToServer.setWebServiceUrl(webServiceUrl);
        checkConnectionToServer.execute();
        while (checkConnectionToServer.isStillProcessing){
            //wait till finish
        }

        this.canConnectToServer = checkConnectionToServer.canConnectToServer;
    }

    private void setUpRequestButton(){

        rescueRequestBean = getUpdatesOfMyRequest();

        if(rescueRequestBean != null){
            if(STATUS_PENDING.equalsIgnoreCase(rescueRequestBean.getStatus())){
                Toast.makeText(MainActivity.this, "YOUR REQUEST IS PENDING" ,
                        Toast.LENGTH_LONG).show();
//                btnRequest.setText("REQUEST IS PENDING");
            }else{
                Toast.makeText(MainActivity.this, "Wait for the assigned rescuer : " + rescueRequestBean.getAssignedRescuer(),
                        Toast.LENGTH_LONG).show();
//                btnRequest.setText("RESCUER IS COMING");
            }
        }else{
//            btnRequest.setText("REQUEST FOR RESCUE");

        }
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnectionToServer();
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create(); //Read Update

                if(canConnectToServer){
                    LayoutInflater inflater = MainActivity.this.getLayoutInflater();



                    alertDialog.setTitle("Send Rescue Request");

                    alertDialog.setView(inflater.inflate(R.layout.rescue_request, null));
                    alertDialog.show();

                    btnSend = (Button)  alertDialog.findViewById(R.id.btnSendRequest);
                    etContactNo = (EditText) alertDialog.findViewById(R.id.etContactNo);
                    etNote = (EditText) alertDialog.findViewById(R.id.etNote);


                    if(rescueRequestBean != null){
                        alertDialog.setTitle("Your request is " + rescueRequestBean.getStatus());
                        btnSend.setText("Update request");
                        etContactNo.setText(rescueRequestBean.getContactNo());
                        etNote.setText(rescueRequestBean.getUserNote());
                    }


                    btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(rescueRequestBean != null){
                                UpdateRescueRequestService updateRescueRequestService = new UpdateRescueRequestService();
                                updateRescueRequestService.setContext(context);
                                updateRescueRequestService.setWebServiceUrl(webServiceUrl);
                                updateRescueRequestService.setRequestId(rescueRequestBean.getRequestId());
                                updateRescueRequestService.setLocation(location != null ? location : rescueRequestBean.getLocation());
                                updateRescueRequestService.setContactNo(etContactNo.getText().toString());
                                updateRescueRequestService.setNote(etNote.getText().toString());
                                updateRescueRequestService.execute();
//                                while (updateRescueRequestService.isStillProcessing){
//                                    //wait until finished
//                                }
                                rescueRequestBean = getUpdatesOfMyRequest();
                            }else{
                                if(location != null){
                                    //send to webservice
                                    SendRescueRequestService sendRescueRequestService = new SendRescueRequestService();
                                    sendRescueRequestService.setContext(context);
                                    sendRescueRequestService.setWebServiceUrl(webServiceUrl);
                                    sendRescueRequestService.setDeviceId(android_id);
                                    sendRescueRequestService.setLocation(location);
                                    sendRescueRequestService.setContactNo(etContactNo.getText().toString());
                                    sendRescueRequestService.setNote(etNote.getText().toString());
                                    sendRescueRequestService.execute();

                                }else{
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    alertDialog.setTitle("Can't find you location");
                                    alertDialog.setMessage("Currently unable to locate your position. Try to move to open area to detect your location.");
                                    alertDialog.show();
                                }
                            }
                            alertDialog.dismiss();
//                            btnRequest.performClick();
                        }
                    });
                }else{
                    alertDialog.setTitle("Can't connect to server");
                    alertDialog.setMessage("Currently unable to connect to server. Please try again later or you may need to update your application.");
                    alertDialog.show();
                }

            }

        });
    }

    private RescueRequestBean getUpdatesOfMyRequest(){
        CheckRequestStatus checkRequestStatus = new CheckRequestStatus();
        checkRequestStatus.setContext(context);
        checkRequestStatus.setWebServiceUrl(webServiceUrl);
        checkRequestStatus.setDeviceId(android_id);
        try {
            checkRequestStatus.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        while (checkRequestStatus.isStillProcessing){
            //wala lang - para lang hintayin matapos yung proccess
        }
        return checkRequestStatus.getRescueRequestBean();
    }

    private void updateRequestLocation(){
        if(rescueRequestBean != null){
            UpdateRescueRequestService updateRescueRequestService = new UpdateRescueRequestService();
            updateRescueRequestService.setContext(context);
            updateRescueRequestService.setWebServiceUrl(webServiceUrl);
            updateRescueRequestService.setRequestId(rescueRequestBean.getRequestId());
            updateRescueRequestService.setLocation(location);
            updateRescueRequestService.setContactNo(rescueRequestBean.getContactNo());
            updateRescueRequestService.setNote(rescueRequestBean.getUserNote());
            updateRescueRequestService.execute();
        }

    }

    private void checkForAdvisoryUpdate(){
        int seconds = 20; // The delay in seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CheckForAdvisory checkForAdvisory = new CheckForAdvisory();
                            checkForAdvisory.setWebServiceUrl(webServiceUrl);
                            checkForAdvisory.setContext(context);
                            checkForAdvisory.execute();
                            while(checkForAdvisory.isStillProcessing){

                            }
                            AdvisoryBean advisoryBean = checkForAdvisory.getAdvisoryBean();

                            FrameLayout frameLayoutAdvisory = (FrameLayout)findViewById(R.id.frameLayoutAdvisory);
                            if(advisoryBean != null){
                                TextView tvAdvisory = (TextView)findViewById(R.id.tvAdvisory);
                                tvAdvisory.setText(advisoryBean.getAdvisoryMessage());
                                frameLayoutAdvisory.setVisibility(View.VISIBLE);
                            }else{
                                frameLayoutAdvisory.setVisibility(View.INVISIBLE);
                            }
                    }
                    });
            }
        }, 0, 1000 * seconds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {



        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();



        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);


        this.location = location.getLatitude() + ", " + location.getLongitude();

        updateRequestLocation();


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));//Moves the camera to users current longitude and latitude
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 15));//Animates camera and zooms to preferred state on the user's current location.


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

//    public class CheckConnectionToServer extends AsyncTask<Void, Void, ConnectionInfo> {
//        boolean canConnectToServer = false;
//        boolean isStillProcessing = true;
//        @Override
//        protected ConnectionInfo doInBackground(Void... params) {
//            isStillProcessing = true;
//            try {
//                String webserviceUrl = getResources().getString(R.string.web_service_url);
//                String url = webserviceUrl + "/checkConnection";
//                RestTemplate restTemplate = new RestTemplate();
//                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//                ConnectionInfo connectionInfo = restTemplate.getForObject(url, ConnectionInfo.class);
//                return connectionInfo;
//            } catch (Exception e) {
//                Log.e("MainActivity", e.getMessage(), e);
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = ProgressDialog.show(context, "Wait",
//                    "Connecting to server...");
//        }
//
//        @Override
//        protected void onPostExecute(ConnectionInfo connectionInfo) {
//            isStillProcessing = false;
//            if(connectionInfo != null){
////                Toast.makeText(MainActivity.this, "Connection status : " + connectionInfo.getStatus() + "\nServer Date & Time : " + connectionInfo.getServerDateTime(), Toast.LENGTH_LONG).show();
//                canConnectToServer = true;
//            }else {
//                canConnectToServer = false;
//            }
//
//            try {
//                if (progressDialog != null) {
//                    progressDialog.dismiss();
//                }
//            } catch (Exception e) {
//                Log.d("Exception", e.toString());
//            }
//
//            setUpRequestButton();
//        }
//
//    }


}


