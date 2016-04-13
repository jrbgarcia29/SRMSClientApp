package com.srms.psu.srmspublicapp.service.httpservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.srms.psu.srmspublicapp.model.ConnectionInfo;
import com.srms.psu.srmspublicapp.model.DeviceBean;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by St Lukes01 on 3/26/2016.
 */
public class CheckConnectionToServer extends AsyncTask<Void, Void, ConnectionInfo> {

    private String webServiceUrl;

    private String deviceId;

    private ConnectionInfo connectionInfo;

    public boolean isStillProcessing = true;

    public boolean canConnectToServer = false;

    private ProgressDialog mProgressDialog;

    private Context context;


    @Override
    protected void onPreExecute() {
        isStillProcessing = true;
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(getContext(), "Wait",
                "Connecting to server...");
    }

    @Override
    protected ConnectionInfo doInBackground(Void... voids) {
        try {
            String url = webServiceUrl + "/checkConnection";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ConnectionInfo connectionInfo = restTemplate.getForObject(url, ConnectionInfo.class);
            this.connectionInfo = connectionInfo;
            if(connectionInfo != null){
                canConnectToServer = true;
            }else {
                canConnectToServer = false;
            }
            isStillProcessing = false;
            return connectionInfo;
        } catch (Exception e) {
            isStillProcessing = false;
            canConnectToServer = false;
            Log.e("MainActivity", e.getMessage(), e);
        }
        return null;
    }


    @Override
    protected void onPostExecute(ConnectionInfo connectionInfo) {
        Log.e("MainActivity", "onPostExecute called", null);
        isStillProcessing = false;

        if(connectionInfo != null){
            canConnectToServer = true;
        }else {
            canConnectToServer = false;
        }

        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
