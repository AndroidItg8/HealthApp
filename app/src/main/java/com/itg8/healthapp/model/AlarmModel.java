package com.itg8.healthapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AlarmModel implements Parcelable {
    private String time;
    private boolean isEnable;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeByte(this.isEnable ? (byte) 1 : (byte) 0);
    }

    public AlarmModel() {
    }

    protected AlarmModel(Parcel in) {
        this.time = in.readString();
        this.isEnable = in.readByte() != 0;
    }

    public static final Parcelable.Creator<AlarmModel> CREATOR = new Parcelable.Creator<AlarmModel>() {
        @Override
        public AlarmModel createFromParcel(Parcel source) {
            return new AlarmModel(source);
        }

        @Override
        public AlarmModel[] newArray(int size) {
            return new AlarmModel[size];
        }
    };
}
