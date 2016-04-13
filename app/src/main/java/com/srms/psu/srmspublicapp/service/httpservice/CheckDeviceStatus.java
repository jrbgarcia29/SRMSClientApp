package com.srms.psu.srmspublicapp.service.httpservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.srms.psu.srmspublicapp.model.DeviceBean;
import com.srms.psu.srmspublicapp.model.RescueRequestBean;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by St Lukes01 on 3/26/2016.
 */
public class CheckDeviceStatus extends AsyncTask<Void, Void, DeviceBean> {

    private String webServiceUrl;

    private String deviceId;

    private DeviceBean deviceBean;

    public boolean isStillProcessing;

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
    protected DeviceBean doInBackground(Void... voids) {
        try {
            String url = webServiceUrl + "/getDeviceByDeviceId/" + deviceId;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            DeviceBean deviceBean1 = restTemplate.getForObject(url, DeviceBean.class);
            isStillProcessing = false;
            this.deviceBean = deviceBean1;
            return deviceBean1;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return null;
    }


    @Override
    protected void onPostExecute(DeviceBean deviceBean) {
        Log.e("MainActivity", "onPostExecute called", null);
        isStillProcessing = false;
        this.deviceBean = deviceBean;

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

    public DeviceBean getDeviceBean() {
        return deviceBean;
    }

    public void setDeviceBean(DeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
