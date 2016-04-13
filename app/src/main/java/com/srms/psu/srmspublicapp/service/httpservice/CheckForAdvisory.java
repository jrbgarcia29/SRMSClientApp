package com.srms.psu.srmspublicapp.service.httpservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.srms.psu.srmspublicapp.model.AdvisoryBean;
import com.srms.psu.srmspublicapp.model.DeviceBean;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jrbgarcia on 4/10/2016.
 */
public class CheckForAdvisory extends AsyncTask<Void, Void, AdvisoryBean> {

    private String webServiceUrl;

    private String deviceId;

    private AdvisoryBean advisoryBean;

    public boolean isStillProcessing = true;

    private ProgressDialog mProgressDialog;

    private Context context;


    @Override
    protected void onPreExecute() {
        isStillProcessing = true;
        super.onPreExecute();

    }

    @Override
    protected AdvisoryBean doInBackground(Void... voids) {
        try {
            String url = webServiceUrl + "/getActiveAdvisory";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            AdvisoryBean advisoryBean = restTemplate.getForObject(url, AdvisoryBean.class);
            isStillProcessing = false;
            this.advisoryBean = advisoryBean;
            return advisoryBean;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return null;
    }


    @Override
    protected void onPostExecute(AdvisoryBean advisoryBean) {
        Log.e("MainActivity", "onPostExecute called", null);
        isStillProcessing = false;
        this.advisoryBean = advisoryBean;

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

    public AdvisoryBean getAdvisoryBean() {
        return advisoryBean;
    }

    public void setAdvisoryBean(AdvisoryBean advisoryBean) {
        this.advisoryBean = advisoryBean;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
