package com.itg8.healthapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BreathModel implements Parcelable {
    private int value;
    private String status;
    private long timestamp;

    public int getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Creator<BreathModel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.value);
        dest.writeString(this.status);
    }

    public BreathModel() {
    }

    protected BreathModel(Parcel in) {
        this.value = in.readInt();
        this.status = in.readString();
    }

    public static final Parcelable.Creator<BreathModel> CREATOR = new Parcelable.Creator<BreathModel>() {
        @Override
        public BreathModel createFromParcel(Parcel source) {
            return new BreathModel(source);
        }

        @Override
        public BreathModel[] newArray(int size) {
            return new BreathModel[size];
        }
    };
}
