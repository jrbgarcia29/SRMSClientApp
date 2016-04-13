package com.srms.psu.srmspublicapp.model;

/**
 * Created by jrbgarcia on 4/10/2016.
 */
public class AdvisoryBean {

    private String advisoryId;
    private String advisoryMessage;
    private String advisoryDate;
    private String status;

    public String getAdvisoryId() {
        return advisoryId;
    }

    public void setAdvisoryId(String advisoryId) {
        this.advisoryId = advisoryId;
    }

    public String getAdvisoryMessage() {
        return advisoryMessage;
    }

    public void setAdvisoryMessage(String advisoryMessage) {
        this.advisoryMessage = advisoryMessage;
    }

    public String getAdvisoryDate() {
        return advisoryDate;
    }

    public void setAdvisoryDate(String advisoryDate) {
        this.advisoryDate = advisoryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
