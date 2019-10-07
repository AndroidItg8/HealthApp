package com.itg8.healthapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SleepModel implements Parcelable {
    public String xAxisValue;
    public float yValue;
    public float xValue;

    public SleepModel(float xValue, float yValue, String xAxisValue) {
        this.xAxisValue = xAxisValue;
        this.yValue = yValue;
        this.xValue = xValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.xAxisValue);
        dest.writeFloat(this.yValue);
        dest.writeFloat(this.xValue);
    }

    protected SleepModel(Parcel in) {
        this.xAxisValue = in.readString();
        this.yValue = in.readFloat();
        this.xValue = in.readFloat();
    }

    public static final Parcelable.Creator<SleepModel> CREATOR = new Parcelable.Creator<SleepModel>() {
        @Override
        public SleepModel createFromParcel(Parcel source) {
            return new SleepModel(source);
        }

        @Override
        public SleepModel[] newArray(int size) {
            return new SleepModel[size];
        }
    };
}
