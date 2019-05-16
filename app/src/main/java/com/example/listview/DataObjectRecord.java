package com.example.listview;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DataObjectRecord implements Comparable<DataObjectRecord>{
    private String ID, employeeCameraID, employeeBuildID, address, detailObject, location;
    private int signID, status, amountImage;
    private Date startDate, finishCameraDate, finishBuildDate;

    public DataObjectRecord(String ID, String employeeCameraID, String employeeBuildID, String address, String detailObject, int signID, int status, Date startDate, Date finishCameraDate, Date finishBuildDate, String location) {
        this.ID = ID;
        this.employeeCameraID = employeeCameraID;
        this.employeeBuildID = employeeBuildID;
        this.address = address;
        this.detailObject = detailObject;
        this.signID = signID;
        this.status = status;
        this.startDate = startDate;
        this.finishCameraDate = finishCameraDate;
        this.finishBuildDate = finishBuildDate;
        this.location = location;
        this.amountImage = 0;
    }

    public DataObjectRecord() {

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmployeeCameraID() {
        return employeeCameraID;
    }

    public void setEmployeeCameraID(String employeeCameraID) {
        this.employeeCameraID = employeeCameraID;
    }

    public String getEmployeeBuildID() {
        return employeeBuildID;
    }

    public void setEmployeeBuildID(String employeeBuildID) {
        this.employeeBuildID = employeeBuildID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSignID() {
        return signID;
    }

    public void setSignID(int signID) {
        this.signID = signID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishCameraDate() {
        return finishCameraDate;
    }

    public void setFinishCameraDate(Date finishCameraDate) {
        this.finishCameraDate = finishCameraDate;
    }

    public Date getFinishBuildDate() {
        return finishBuildDate;
    }

    public void setFinishBuildDate(Date finishBuildDate) {
        this.finishBuildDate = finishBuildDate;
    }

    public String getDetailObject() {
        return detailObject;
    }

    public void setDetailObject(String detailObject) {
        this.detailObject = detailObject;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAmountImage() {
        return amountImage;
    }

    public void setAmountImage(int amountImage) {
        this.amountImage = amountImage;
    }

    public void addAmountImage(int amountImage) {this.amountImage += amountImage;}

    public String toString() {
        return status + "," + ID ;
    }

    @Override
    public int compareTo(DataObjectRecord o) {
        return o.toString().compareTo(toString());
    }
}
