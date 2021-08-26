package com.example.chaudelivery.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation implements Parcelable {

    private GeoPoint geo_point;
    private @ServerTimestamp
    Date timestamp;
    private com.example.chaudelivery.model.User user;


    public UserLocation() {

    }

    public UserLocation(GeoPoint geo_point, Date timestamp, com.example.chaudelivery.model.User user) {
        this.geo_point = geo_point;
        this.timestamp = timestamp;
        this.user = user;
    }


    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
    }


    @Override
    public int describeContents() {
        return 0;
    }


    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }


    public com.example.chaudelivery.model.User getUser() {
        return user;
    }

    public void setUser(com.example.chaudelivery.model.User user) {
        this.user = user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    protected UserLocation(Parcel in) {
        user = in.readParcelable(com.example.chaudelivery.model.User.class.getClassLoader());
    }


    @Override
    public String toString() {
        return "UserLocation{" +
                "geo_point=" + geo_point +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                '}';
    }
}
