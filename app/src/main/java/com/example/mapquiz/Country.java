package com.example.mapquiz;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

public class Country implements Parcelable {

    private String name;
    private String flagUrl;
    private String capital;
    private String region;
    private String alt; // Flag description

    public Country(JSONObject jsonObject) throws JSONException {
        JSONObject nameObject = jsonObject.getJSONObject("name");
        this.name = nameObject.getString("common");
        this.flagUrl = jsonObject.getJSONObject("flags").getString("png");
        this.capital = jsonObject.getJSONArray("capital").getString(0);
        this.region = jsonObject.getString("region");
        this.alt = jsonObject.getJSONObject("flags").getString("alt");
    }

    public String getName() {
        return name;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public String getCapital() {
        return capital;
    }

    public String getRegion() {
        return region;
    }

    public String getAlt() {
        return alt;
    }

    protected Country(Parcel in) {
        name = in.readString();
        flagUrl = in.readString();
        capital = in.readString();
        region = in.readString();
        alt = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(flagUrl);
        dest.writeString(capital);
        dest.writeString(region);
        dest.writeString(alt);
    }
}