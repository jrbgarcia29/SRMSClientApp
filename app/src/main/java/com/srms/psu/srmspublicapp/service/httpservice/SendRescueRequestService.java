package com.srms.psu.srmspublicapp.service.httpservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.srms.psu.srmspublicapp.R;
import com.srms.psu.srmspublicapp.model.ConnectionInfo;
import com.srms.psu.srmspublicapp.model.RescueRequestBean;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jrbgarcia on 2/6/2016.
 */
public class SendRescueRequestService extends AsyncTask<Void, Void, RescueRequestBean> {

    private String webServiceUrl;
    private String deviceId;
    private String location;
    private String contactNo;
    private String note;

    private RescueRequestBean rescueRequestBean;

    private Context context;

    private ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(getContext(), "Sending",
                "Sending rescue request...");
    }

    @Override
    protected RescueRequestBean doInBackground(Void... voids) {
        try {
            String url = webServiceUrl + "/newRescueRequest/" + deviceId + "/" + location + "/" + contactNo + "/" + note;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            RescueRequestBean rescueRequestBean = restTemplate.getForObject(url, RescueRequestBean.class);
            return rescueRequestBean;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(RescueRequestBean rescueRequestBean) {
        this.rescueRequestBean = rescueRequestBean;
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setRescueRequestBean(RescueRequestBean rescueRequestBean) {
        this.rescueRequestBean = rescueRequestBean;
    }

    public RescueRequestBean getRescueRequestBean() {
        return rescueRequestBean;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
