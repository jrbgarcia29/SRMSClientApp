package com.srms.psu.srmspublicapp.model;

/**
 * Created by jrbgarcia on 4/10/2016.
 */
public class DeviceBean {

    private String id;
    private String deviceId;
    private String ownerLastName;
    private String ownerFirstName;
    private String ownerMi;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerMi() {
        return ownerMi;
    }

    public void setOwnerMi(String ownerMi) {
        this.ownerMi = ownerMi;
    }
}
