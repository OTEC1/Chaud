package com.example.chaudelivery.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String email, user_id, username,
             name,phone,member_T,
             img_url,token,delivery_details;


    private  int fair,good,bad;



   public User(){

    }


    public User(Parcel in) {
       name = in.readString();
        email = in.readString();
        user_id = in.readString();
        username = in.readString();
        img_url =in.readString();
        token =in.readString();
        good =in.readInt();
        fair =in.readInt();
        bad =in.readInt();
        delivery_details =in.readString();

    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDelivery_details() {
        return delivery_details;
    }

    public void setDelivery_details(String delivery_details) {
        this.delivery_details = delivery_details;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMember_T() {
        return member_T;
    }

    public void setMember_T(String member_T) {
        this.member_T = member_T;
    }


    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getFair() {
        return fair;
    }

    public void setFair(int fair) {
        this.fair = fair;
    }

    public int getBad() {
        return bad;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(img_url);
        dest.writeString(token);
        dest.writeInt(good);
        dest.writeInt(fair);
        dest.writeInt(bad);
        dest.writeString(delivery_details);

    }
}

