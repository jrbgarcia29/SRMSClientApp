package com.srms.psu.srmspublicapp.model;

/**
 * Created by jrbgarcia on 2/6/2016.
 */
public class RescueRequestBean {

    private String requestId;
    private String status;
    private String requestDateTime;
    private String deviceId;
    private String location;
    private String contactNo;
    private String userNote;
    private String remarks;
    private String receivedByStaffId;
    private String receivedBy;
    private String assignedRescuerId;
    private String assignedRescuer;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReceivedByStaffId() {
        return receivedByStaffId;
    }

    public void setReceivedByStaffId(String receivedByStaffId) {
        this.receivedByStaffId = receivedByStaffId;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getAssignedRescuerId() {
        return assignedRescuerId;
    }

    public void setAssignedRescuerId(String assignedRescuerId) {
        this.assignedRescuerId = assignedRescuerId;
    }

    public String getAssignedRescuer() {
        return assignedRescuer;
    }

    public void setAssignedRescuer(String assignedRescuer) {
        this.assignedRescuer = assignedRescuer;
    }
}
