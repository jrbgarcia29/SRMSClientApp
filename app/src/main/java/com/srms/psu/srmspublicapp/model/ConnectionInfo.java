package com.srms.psu.srmspublicapp.model;

/**
 * Created by St Lukes01 on 2/6/2016.
 */
public class ConnectionInfo {
    private String status;
    private long serverDateTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getServerDateTime() {
        return serverDateTime;
    }

    public void setServerDateTime(long serverDateTime) {
        this.serverDateTime = serverDateTime;
    }
}
