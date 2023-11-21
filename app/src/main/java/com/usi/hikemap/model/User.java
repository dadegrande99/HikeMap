package com.usi.hikemap.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class User implements Parcelable {

    private String name, surname, username, password, auth, userId, provider;


    public User(String name, String surname, String username, String auth, String password, String userId,
                String provider){
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.auth = auth;
        this.password = password;
        this.userId = userId;
        this.provider = provider;
    }

    public User() {
    }


    protected User(Parcel in) {
        name = in.readString();
        surname = in.readString();
        username = in.readString();
        auth = in.readString();
        password = in.readString();
        userId = in.readString();
        provider = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    @Exclude
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", username='" + username + '\'' +
                ", auth='" + auth + '\'' +
                ", password='" + password + '\'' +
                ", userId='" + userId + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.surname);
        parcel.writeString(this.username);
        parcel.writeString(this.auth);
        parcel.writeString(this.password);
        parcel.writeString(this.userId);
        parcel.writeString(this.provider);
    }
}

