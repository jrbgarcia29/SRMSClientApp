package com.srms.psu.srmspublicapp.service.httpservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.srms.psu.srmspublicapp.model.RescueRequestBean;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by St Lukes01 on 2/7/2016.
 */
public class CheckRequestStatus extends AsyncTask<Void, Void, RescueRequestBean> {

    private String webServiceUrl;

    private String deviceId;

    private RescueRequestBean rescueRequestBean;

    public boolean isStillProcessing = true;

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
    protected RescueRequestBean doInBackground(Void... voids) {
        try {
            String url = webServiceUrl + "/getCurrentRequest/" + deviceId;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            RescueRequestBean rescueRequestBean = restTemplate.getForObject(url, RescueRequestBean.class);
            isStillProcessing = false;
            this.rescueRequestBean = rescueRequestBean;
            return rescueRequestBean;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return null;
    }


    @Override
    protected void onPostExecute(RescueRequestBean rescueRequestBean) {
        Log.e("MainActivity", "onPostExecute called", null);
        isStillProcessing = false;
        this.rescueRequestBean = rescueRequestBean;

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

    public RescueRequestBean getRescueRequestBean() {
        return rescueRequestBean;
    }

    public void setRescueRequestBean(RescueRequestBean rescueRequestBean) {
        this.rescueRequestBean = rescueRequestBean;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
