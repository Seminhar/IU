package com.myim.Beans;

import java.io.Serializable;

/**
 * Created by PC on 2015/5/3.
 */
public class NearByUser  implements Serializable{
    private String userName;
    private double lat;
    private double lng;
    private String detail;
    private double distance ;
    private User user;
    public NearByUser() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    public String toString() {
        return userName + ","  + lat + ","  +lng + ","  + detail + ","  + distance  ;
    }
    public NearByUser(String userName, double lat, double lng, String detail,
                      double distance,User user) {
        super();
        this.userName = userName;
        this.lat = lat;
        this.lng = lng;
        this.detail = detail;
        this.distance = distance;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }


}